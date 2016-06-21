package com.insuleto.koloro.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.insuleto.koloro.KoloroApplication;
import com.insuleto.koloro.service.KoloroService;
import com.insuleto.koloro.util.FirebaseEvents;
import javax.inject.Inject;

public class QuickLaunchActivity extends BaseActivity {

  @Inject MediaProjectionManager mediaProjectionManager;
  @Inject FirebaseAnalytics firebaseAnalytics;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //todo this
    KoloroApplication.get(this).applicationComponent().inject(this);

    Intent mediaCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
    startActivityForResult(mediaCaptureIntent, CREATE_SCREEN_CAPTURE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CREATE_SCREEN_CAPTURE) {
      if (resultCode == Activity.RESULT_OK) {
        Intent serviceIntent = KoloroService.intent(this, resultCode, data);
        firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_PERMISSION_GRANTED, null);
        startService(serviceIntent);
      } else {
        Log.d(TAG, "Couldn't get permission to screen capture");
        firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_PERMISSION_DENIED, null);
      }
      finish();
    }
  }
}
