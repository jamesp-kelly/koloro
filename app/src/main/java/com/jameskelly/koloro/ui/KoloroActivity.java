package com.jameskelly.koloro.ui;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.model.KoloroObj;
import com.jameskelly.koloro.service.KoloroService;
import com.jameskelly.koloro.ui.adaptors.ColorRecyclerAdapter;
import com.jameskelly.koloro.ui.adaptors.ColorRecyclerAdapter.ColorItemListener;
import com.jameskelly.koloro.ui.presenters.KoloroPresenter;
import com.jameskelly.koloro.ui.views.KoloroView;
import com.jameskelly.koloro.util.FirebaseEvents;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import javax.inject.Inject;

public class KoloroActivity extends BaseActivity implements KoloroView {

  private static final int CREATE_SCREEN_CAPTURE = 1255;

  private ColorRecyclerAdapter colorRecyclerAdapter;

  @Inject MediaProjectionManager mediaProjectionManager;
  @Inject KoloroPresenter presenter;
  @Inject FirebaseAnalytics firebaseAnalytics;

  @BindDimen(R.dimen.prefs_layout_margin_top) int prefsLayoutMarginTop;
  @BindView(R.id.prefs_button) ImageButton prefsButton;
  @BindView(R.id.prefs_layout) ViewGroup prefsLayout;
  @BindView(R.id.start_capture) Button startCaptureButton;
  @BindView(R.id.saved_colors_recycler) RecyclerView savedColorsRecycler;
  @BindView(R.id.saved_colors_button) Button savedColorsButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_koloro);
    KoloroApplication.get(this).applicationComponent().inject(this);
    ButterKnife.bind(this);

    prefsLayout.setEnabled(false);

    presenter.bindView(this);
    presenter.setupRealm();

    savedColorsRecycler.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    colorRecyclerAdapter = new ColorRecyclerAdapter(presenter.getAllKoloroObjects(),
        listener, LinearLayoutManager.HORIZONTAL);
    savedColorsRecycler.setAdapter(colorRecyclerAdapter);
  }

  ColorItemListener listener = new ColorItemListener() {
    @Override public void colorTextChanged(int backgroundColor, TextView... affectedViews) {

    }

    @Override public void noteButtonClicked(KoloroObj koloroObj) {

    }

    @Override public void copyButtonClicked(String hexString) {
      firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_BUTTON_CLICKED, null);
    }
  };


  @OnClick(R.id.prefs_button)
  void onPrefsClicked(View v) {
    if (prefsLayout.getVisibility() == View.INVISIBLE) {
      layoutExpandAnimation(true, null);
    } else {
      layoutExpandAnimation(false, new SupportAnimator.AnimatorListener() {
        @Override public void onAnimationStart() {
        }

        @Override public void onAnimationEnd() {
          prefsLayout.setVisibility(View.INVISIBLE);
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
    startActivityForResult(mediaCaptureIntent, CREATE_SCREEN_CAPTURE);
  }

  @OnClick(R.id.saved_colors_button)
  void onSavedColorsClicked(View v) {
    if (savedColorsRecycler.getVisibility() == View.INVISIBLE) {

      savedColorsRecycler.setTranslationY(savedColorsRecycler.getHeight());

      savedColorsRecycler.animate()
          .translationY(0)
          .setListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
              savedColorsRecycler.setVisibility(View.VISIBLE);
            }

            @Override public void onAnimationEnd(Animator animation) {
            }

            @Override public void onAnimationCancel(Animator animation) {
            }

            @Override public void onAnimationRepeat(Animator animation) {
            }
          });
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CREATE_SCREEN_CAPTURE) {
      if (resultCode == Activity.RESULT_OK) {
        Intent intent = KoloroService.intent(this, resultCode, data);
        startService(intent);
        firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_PERMISSION_GRANTED, null);
      } else {
        Log.d(TAG, "Couldn't get permission to screen capture");
        firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_PERMISSION_DENIED, null);
        //show message to user
      }
    }
  }

  @Override public void updateColorList() {
    colorRecyclerAdapter.notifyDataSetChanged();
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
