package com.jameskelly.koloro.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.ScreenCaptureManager;
import com.jameskelly.koloro.events.ImageProcessedEvent;
import com.jameskelly.koloro.preferences.BooleanPreference;
import com.jameskelly.koloro.preferences.IntPreference;
import com.jameskelly.koloro.preferences.PreferencesModule;
import com.jameskelly.koloro.ui.ColorPickActivity;
import com.jameskelly.koloro.ui.KoloroActivity;
import com.jameskelly.koloro.ui.layouts.OverlayButtonsLayout;
import com.jameskelly.koloro.ui.layouts.ScreenCaptureFlashLayout;
import com.jameskelly.koloro.util.FirebaseEvents;
import javax.inject.Inject;
import javax.inject.Named;
import org.greenrobot.eventbus.EventBus;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class KoloroService extends Service {

  private static final String EXTRA_RESULT_CODE = "result_code";
  private static final String EXTRA_DATA = "data";
  private static final String STOP_KOLORO = "com.jameskelly.koloro.StopService";
  private static final int KOLORA_NOTIFICATION_ID = 1406;
  private static final int VIBRATE_DURATION = 50;

  private OverlayButtonsLayout overlayButtonsLayout;
  private WindowManager.LayoutParams buttonsParams;
  private ScreenCaptureFlashLayout screenCaptureFlashLayout;
  private WindowManager.LayoutParams loadingParams;
  private ScreenCaptureManager screenCaptureManager;
  private Toast captureToast;

  @Inject @Named(PreferencesModule.SHOW_NOTIFICATION_KEY) BooleanPreference showNotificationPref;
  @Inject @Named(PreferencesModule.CAPTURE_BUTTON_POSITION_KEY)
  IntPreference captureButtonPositionPreference;
  @Inject @Named(PreferencesModule.VIBRATION_KEY) BooleanPreference vibrationPref;
  @Inject WindowManager windowManager;
  @Inject NotificationManager notificationManager;
  @Inject Vibrator vibrator;
  @Inject FirebaseAnalytics firebaseAnalytics;


  public static Intent intent(Context context, int resultCode, Intent data) {
    Intent intent = new Intent(context, KoloroService.class);
    intent.putExtra(EXTRA_RESULT_CODE, resultCode);
    intent.putExtra(EXTRA_DATA, data);
    return intent;
  }

  @Override public void onCreate() {
    IntentFilter filter = new IntentFilter(STOP_KOLORO);
    registerReceiver(koloroReciever, filter);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
    Intent resultData = intent.getParcelableExtra(EXTRA_DATA);

    if (resultCode == 0 || resultData == null) {
      throw new IllegalStateException();
    }

    KoloroApplication.get(this).applicationComponent().inject(this);
    screenCaptureManager = new ScreenCaptureManager(this, resultCode, resultData);

    overlayButtonsLayout = new OverlayButtonsLayout(this, layoutOverlayClickListener);
    buttonsParams = overlayButtonsLayout.layoutParams(captureButtonPositionPreference.get());
    screenCaptureFlashLayout = new ScreenCaptureFlashLayout(this, this::removeFlashStartWork);
    loadingParams = screenCaptureFlashLayout.layoutParams();
    captureToast = Toast.makeText(KoloroService.this,
        R.string.screen_capture_toast, Toast.LENGTH_LONG);

    setForground();
    showButtonsOverlay();

    return START_NOT_STICKY;
  }

  private void showButtonsOverlay() {
    windowManager.addView(overlayButtonsLayout, buttonsParams);
  }

  private void removeButtonsOverlay() {
    if (overlayButtonsLayout != null && overlayButtonsLayout.isAttachedToWindow()) {
      windowManager.removeView(overlayButtonsLayout);
    }
  }

  private void showScreenCaptureFlash() {
    windowManager.addView(screenCaptureFlashLayout, loadingParams);
    vibrate();
  }

  private void vibrate() {
    if (vibrationPref.get()) {
      vibrator.vibrate(VIBRATE_DURATION);
    }
  }

  private void removeScreenCaptureFlash() {
    if (screenCaptureFlashLayout != null && screenCaptureFlashLayout.isAttachedToWindow()) {
      windowManager.removeView(screenCaptureFlashLayout);
    }
  }

  private void removeFlashStartWork() {
    removeScreenCaptureFlash();
    screenCaptureManager.captureCurrentScreen(imageCaptureListener);
  }

  private void setForground() {
    Intent homeIntent = new Intent(this, KoloroActivity.class);
    homeIntent.setAction(Intent.ACTION_MAIN);
    homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);

    Intent stopIntent = new Intent(STOP_KOLORO);

    PendingIntent homePendingIntent = PendingIntent.getActivity(this, 0, homeIntent, 0);
    PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);

    Notification.Builder builder = new Notification.Builder(this).setContentTitle(getString(R.string.notification_title))
        .setContentText(getString(R.string.notification_text))
        .setSmallIcon(R.drawable.ic_palette_white_24dp)
        .setTicker(getString(R.string.notification_title))
        .setContentIntent(homePendingIntent)
        .setDeleteIntent(stopPendingIntent)
        .setCategory(Notification.CATEGORY_SERVICE); //add cancel button?

    startForeground(KOLORA_NOTIFICATION_ID, builder.build());
  }

  private void finishService() {
    stopForeground(true);
    cleanupOverlays();
    stopSelf();
  }

  private void cleanupOverlays() {
    if (overlayButtonsLayout != null) {
      if (overlayButtonsLayout.isAttachedToWindow()) {
        windowManager.removeView(overlayButtonsLayout);
      }
      overlayButtonsLayout = null;
    }
    if (screenCaptureFlashLayout != null) {
      if (screenCaptureFlashLayout.isAttachedToWindow()) {
        windowManager.removeView(screenCaptureFlashLayout);
      }
      screenCaptureFlashLayout = null;
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(koloroReciever);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private OverlayButtonsLayout.OverlayClickListener
      layoutOverlayClickListener = new OverlayButtonsLayout.OverlayClickListener() {
    @Override public void onCaptureClicked() {
      removeButtonsOverlay();
      captureToast.show();
      vibrate();
      firebaseAnalytics.logEvent(FirebaseEvents.OVERLAY_CAPTURE_CLICKED, null);

      screenCaptureManager.captureCurrentScreen(imageCaptureListener);
    }

    @Override public void onCancelClicked() {
      finishService();
      firebaseAnalytics.logEvent(FirebaseEvents.OVERLAY_CANCEL_CLICKED, null);
    }
  };

  private ScreenCaptureManager.ImageCaptureListener imageCaptureListener =
      new ScreenCaptureManager.ImageCaptureListener() {
        @Override public void onImageCaptured(Bitmap capturedImage) {

          saveBitmapToGallery(capturedImage);

          Intent intent = ColorPickActivity.intent(KoloroService.this);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
              | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY);
          startActivity(intent);
        }

        @Override public void onImageCaptureError() {
        }
      };

  private void saveBitmapToGallery(Bitmap bitmap) {
    screenCaptureManager.getSavedBitmapUriObservable(bitmap)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Uri>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            Toast.makeText(KoloroService.this, "Error saving screenshot", Toast.LENGTH_SHORT).show();
          }

          @Override public void onNext(Uri uri) {
            EventBus.getDefault().postSticky(new ImageProcessedEvent(true, uri, captureToast));
            finishService();
          }
        });
  }

  private final BroadcastReceiver koloroReciever = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(STOP_KOLORO)) {
        finishService();
      }
    }
  };

}
