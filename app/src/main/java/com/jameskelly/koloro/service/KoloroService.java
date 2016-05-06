package com.jameskelly.koloro.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.ui.OverlayFrameLayout;
import javax.inject.Inject;

public class KoloroService extends Service {

  private static final String EXTRA_RESULT_CODE = "result_code";
  private static final String EXTRA_DATA = "data";

  private OverlayFrameLayout overlayFrameLayout;

  @Inject WindowManager windowManager;


  public static Intent intent(Context context, int resultCode, Intent data) {
    Intent intent = new Intent(context, KoloroService.class);
    intent.putExtra(EXTRA_RESULT_CODE, resultCode);
    intent.putExtra(EXTRA_DATA, data);
    return intent;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
    Intent dataIntent = intent.getParcelableExtra(EXTRA_DATA);

    if (resultCode == 0 || dataIntent == null) {
      throw new IllegalStateException();
    }

    KoloroApplication.get(this).applicationComponent().inject(this);

    overlayFrameLayout = new OverlayFrameLayout(this, new OverlayFrameLayout.Listener() {
      @Override public void onCapture() {
        Toast.makeText(KoloroService.this, "capture clicked", Toast.LENGTH_SHORT).show();
      }

      @Override public void onCancel() {
        removeOverlayView();
      }
    });

    windowManager.addView(overlayFrameLayout, overlayFrameLayout.setupCaptureWindowLayoutParams());

    return START_NOT_STICKY;
  }

  private void removeOverlayView() {
    if (overlayFrameLayout != null) {
      windowManager.removeView(overlayFrameLayout);
      overlayFrameLayout = null;
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
