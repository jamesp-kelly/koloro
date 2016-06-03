package com.jameskelly.koloro.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.preferences.SimpleSpinnerAdapter;
import com.jameskelly.koloro.service.KoloroService;
import com.jameskelly.koloro.ui.presenters.KoloroPresenter;
import com.jameskelly.koloro.ui.views.KoloroView;
import com.jameskelly.koloro.util.FirebaseEvents;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import javax.inject.Inject;

public class KoloroActivity extends BaseActivity implements KoloroView {

  private static final int CREATE_SCREEN_CAPTURE = 1255;
  private static final int OVERLAY_PERMISSION = 1305;

  private SimpleSpinnerAdapter captureButtonPositionAdapter;

  @Inject MediaProjectionManager mediaProjectionManager;
  @Inject KoloroPresenter presenter;
  @Inject FirebaseAnalytics firebaseAnalytics;

  @BindDimen(R.dimen.prefs_layout_margin_top) int prefsLayoutMarginTop;
  @BindView(R.id.prefs_button) ImageButton prefsButton;
  @BindView(R.id.prefs_layout) ViewGroup prefsLayout;
  @BindView(R.id.start_capture) Button startCaptureButton;
  @BindView(R.id.saved_colors_button) Button savedColorsButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_koloro);
    KoloroApplication.get(this).applicationComponent().inject(this);
    ButterKnife.bind(this);

    //prefsLayout.setEnabled(false);
    presenter.bindView(this);
    presenter.setupRealm();


    getSupportFragmentManager().beginTransaction()
        .replace(R.id.prefs_layout, new PreferenceFragment())
        .commit();
  }


  @OnClick(R.id.prefs_button)
  void onPrefsClicked(View v) {
    if (prefsLayout.getVisibility() == View.INVISIBLE) {
      layoutExpandAnimation(true, new SupportAnimator.AnimatorListener() {
        @Override public void onAnimationStart() {
        }

        @Override public void onAnimationEnd() {
          //prefsLayout.setEnabled(true);
        }

        @Override public void onAnimationCancel() {
        }

        @Override public void onAnimationRepeat() {
        }
      });
    } else {
      layoutExpandAnimation(false, new SupportAnimator.AnimatorListener() {
        @Override public void onAnimationStart() {
        }

        @Override public void onAnimationEnd() {
          prefsLayout.setVisibility(View.INVISIBLE);
          //prefsLayout.setEnabled(false);
        }
        @Override public void onAnimationCancel() {}
        @Override public void onAnimationRepeat() {}
      });
    }
  }

  private void layoutExpandAnimation(boolean reveal, SupportAnimator.AnimatorListener listener) {
    int x = prefsLayout.getWidth();
    int y = 0;
    float maxRadius = (float) Math.hypot(prefsLayout.getWidth(), prefsLayout.getHeight());

    SupportAnimator animator = ViewAnimationUtils.createCircularReveal
        (prefsLayout, x, y, (reveal ? 200 : maxRadius), (reveal ? maxRadius : 200));
    animator.setInterpolator(new AccelerateDecelerateInterpolator());
    prefsLayout.setVisibility(reveal ? View.VISIBLE : prefsLayout.getVisibility());
    if (listener != null) {
      animator.addListener(listener);
    }

    animator.start();
  }

  @OnClick(R.id.start_capture)
  void onStartCaptureClicked() {
    Intent mediaCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
      //need to request permission to draw overlays
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
          Uri.parse("package:" + getPackageName()));
      startActivityForResult(intent, OVERLAY_PERMISSION);
    } else {
      startActivityForResult(mediaCaptureIntent, CREATE_SCREEN_CAPTURE);
    }
  }

  //@OnClick(R.id.help_button)
  //void onHelpClicked() {
  //  Toast.makeText(this, "This doesn't do anything right now", Toast.LENGTH_SHORT).show();
  //}

  @OnTouch(R.id.prefs_layout)
  boolean onPrefsLayoutTouched() {
    return true;
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
        //show message to user
      }
    } else if (requestCode == OVERLAY_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.canDrawOverlays(this)) {
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), CREATE_SCREEN_CAPTURE);
      } else {
        Toast.makeText(this, "Couldn't get permission to display overlay", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override protected void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override protected void onDestroy() {
    presenter.unbindView(this);
    super.onDestroy();
  }
}
