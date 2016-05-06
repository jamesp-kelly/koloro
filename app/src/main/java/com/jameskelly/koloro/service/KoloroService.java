package com.jameskelly.koloro.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.ScreenCaptureManager;
import com.jameskelly.koloro.preferences.BooleanPreference;
import com.jameskelly.koloro.preferences.PreferencesModule;
import com.jameskelly.koloro.ui.KoloroActivity;
import com.jameskelly.koloro.ui.OverlayFrameLayout;
import javax.inject.Inject;
import javax.inject.Named;

public class KoloroService extends Service {

  private static final String EXTRA_RESULT_CODE = "result_code";
  private static final String EXTRA_DATA = "data";
  private static final String STOP_KOLORO = "com.jameskelly.koloro.StopService";
  private static final int KOLORA_NOTIFICATION_ID = 1406;

  private OverlayFrameLayout overlayFrameLayout;
  private ScreenCaptureManager screenCaptureManager;

  @Inject @Named(PreferencesModule.SHOW_NOTIFICATION_KEY) BooleanPreference showNotificationPref;
  @Inject WindowManager windowManager;
  @Inject NotificationManager notificationManager;


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

    overlayFrameLayout = new OverlayFrameLayout(this, layoutOverlayClickListener);
    windowManager.addView(overlayFrameLayout, overlayFrameLayout.setupCaptureWindowLayoutParams());

    screenCaptureManager = new ScreenCaptureManager(this, resultCode, resultData, imageCaptureListener);

    if (showNotificationPref.get()) {
      sendNotification();
    }

    return START_NOT_STICKY;
  }

  private void removeOverlayView() {
    if (overlayFrameLayout != null) {
      windowManager.removeView(overlayFrameLayout);
      overlayFrameLayout = null;
    }
  }

  private void sendNotification() {

    Intent homeIntent = new Intent(this, KoloroActivity.class);
    homeIntent.setAction(Intent.ACTION_MAIN);
    homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);

    Intent stopIntent = new Intent(STOP_KOLORO);

    PendingIntent homePendingIntent = PendingIntent.getActivity(this, 0, homeIntent, 0);
    PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);

    Notification.Builder builder = new Notification.Builder(this)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(getString(R.string.notification_text))
        .setSmallIcon(android.R.drawable.btn_radio)
        .setTicker(getString(R.string.notification_ticker))
        .setContentIntent(homePendingIntent)
        .setDeleteIntent(stopPendingIntent)
        .setCategory(Notification.CATEGORY_SERVICE);

    notificationManager.notify(KOLORA_NOTIFICATION_ID, builder.build());
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(koloroReciever);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private OverlayFrameLayout.OverlayClickListener
      layoutOverlayClickListener = new OverlayFrameLayout.OverlayClickListener() {
    @Override public void onCaptureClicked() {
      screenCaptureManager.captureScreen();
    }

    @Override public void onCancelClicked() {
      removeOverlayView();
      KoloroService.this.stopSelf();
    }
  };

  private ScreenCaptureManager.ImageCaptureListener imageCaptureListener =
      new ScreenCaptureManager.ImageCaptureListener() {
        @Override public void onImageCaptured(String imageUri) {
          Toast.makeText(KoloroService.this, imageUri, Toast.LENGTH_SHORT).show();
          //Intent intent = new Intent();
          //intent.putExtra("", imageUri);
          //startActivity(intent);
          //KoloroService.this.stopSelf();
        }

        @Override public void onImageCaptureError() {
          Toast.makeText(KoloroService.this, "error", Toast.LENGTH_SHORT).show();
          removeOverlayView();
          KoloroService.this.stopSelf();
        }
      };

  private final BroadcastReceiver koloroReciever = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(STOP_KOLORO)) {
        removeOverlayView();
        KoloroService.this.stopSelf();
      }
    }
  };

}
