package com.jameskelly.koloro.ui;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.preferences.BooleanPreference;
import com.jameskelly.koloro.preferences.IntPreference;
import com.jameskelly.koloro.preferences.PreferencesModule;
import com.jameskelly.koloro.preferences.SimpleSpinnerAdapter;
import com.jameskelly.koloro.service.KoloroService;
import com.jameskelly.koloro.ui.layouts.AnimatorEndListener;
import com.jameskelly.koloro.ui.views.KoloroView;
import javax.inject.Inject;
import javax.inject.Named;

public class KoloroActivity extends BaseActivity implements KoloroView {

  private static final int CREATE_SCREEN_CAPTURE = 1255;

  private SimpleSpinnerAdapter captureButtonPositionAdapter;

  @Inject @Named(PreferencesModule.SHOW_NOTIFICATION_KEY)
  BooleanPreference showNotificationPreference;
  @Inject @Named(PreferencesModule.CAPTURE_BUTTON_VISIBLE_KEY)
  BooleanPreference captureButtonVisiblePreference;
  @Inject @Named(PreferencesModule.STORE_CAPTURES_IN_GALLERY_KEY)
  BooleanPreference storeCapturesInGalleryPreference;
  @Inject @Named(PreferencesModule.MULTI_SHOT_KEY)
  BooleanPreference multiShotPreference;
  @Inject @Named(PreferencesModule.CAPTURE_BUTTON_POSITION_KEY)
  IntPreference captureButtonPositionPreference;
  @Inject MediaProjectionManager mediaProjectionManager;

  @BindDimen(R.dimen.prefs_layout_margin_top) int prefsLayoutMarginTop;

  @BindView(R.id.prefs_button) ImageButton prefsButton;
  @BindView(R.id.prefs_layout) ViewGroup prefsLayout;

  //@BindView(R.id.switch_show_notification) Switch showNotificationSwitch;
  //@BindView(R.id.switch_capture_button_visible) Switch captureButtonVisibleSwitch;
  //@BindView(R.id.switch_store_captures) Switch storeCapturesSwitch;
  //@BindView(R.id.switch_multi_shot) Switch multiShotSwitch;
  //@BindView(R.id.spinner_capture_button_position) Spinner captureButtonPositionSpinner;
  @BindView(R.id.start_capture) Button startCaptureButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_koloro);
    KoloroApplication.get(this).applicationComponent().inject(this);
    ButterKnife.bind(this);

    prefsLayout.setEnabled(false);

    bindSharedPreferences();
  }

  @Override public void bindSharedPreferences() {
    //showNotificationSwitch.setChecked(showNotificationPreference.get());
    //captureButtonVisibleSwitch.setChecked(captureButtonVisiblePreference.get());
    //storeCapturesSwitch.setChecked(storeCapturesInGalleryPreference.get());
    //multiShotSwitch.setChecked(multiShotPreference.get());

    //captureButtonPositionAdapter = new SimpleSpinnerAdapter(this, buttonPositionValues());
    //captureButtonPositionSpinner.setAdapter(captureButtonPositionAdapter);
    //captureButtonPositionSpinner.setSelection(captureButtonPositionPreference.get());
  }

  //private List<String> buttonPositionValues() {
  //  String[] stringArray = getResources().getStringArray(R.array.capture_button_position_array);
  //  return Arrays.asList(stringArray);
  //}

  //@OnCheckedChanged(R.id.switch_show_notification)
  //void onShowNotificationChanged() {
  //  boolean newValue = showNotificationSwitch.isChecked();
  //  boolean oldValue = showNotificationPreference.get();
  //
  //  if (newValue != oldValue) {
  //    Log.d(TAG, String.format("Updating 'show notification' preference to %s", newValue));
  //    showNotificationPreference.set(newValue);
  //  }
  //}
  //
  //@OnCheckedChanged(R.id.switch_capture_button_visible)
  //void onCaptureButtonChanged() {
  //  boolean newValue = captureButtonVisibleSwitch.isChecked();
  //  boolean oldValue = captureButtonVisiblePreference.get();
  //
  //  if (newValue != oldValue) {
  //    Log.d(TAG, String.format("Updating 'capture button visible' preference to %s", newValue));
  //    captureButtonVisiblePreference.set(newValue);
  //  }
  //}
  //
  //@OnCheckedChanged(R.id.switch_store_captures)
  //void onStoreCapturesChanged() {
  //  boolean newValue = storeCapturesSwitch.isChecked();
  //  boolean oldValue = storeCapturesInGalleryPreference.get();
  //
  //  if (newValue != oldValue) {
  //    Log.d(TAG, String.format("Updating 'store captures' preference to %s", newValue));
  //    storeCapturesInGalleryPreference.set(newValue);
  //  }
  //}
  //
  //@OnCheckedChanged(R.id.switch_multi_shot)
  //void onMultiShotChanged() {
  //  boolean newValue = multiShotSwitch.isChecked();
  //  boolean oldValue = multiShotPreference.get();
  //
  //  if (newValue != oldValue) {
  //    Log.d(TAG, String.format("Updating 'store captures' preference to %s", newValue));
  //    multiShotPreference.set(newValue);
  //  }
  //}
  //
  //@OnItemSelected(R.id.spinner_capture_button_position)
  //void onCaptureMethodChanged(int position) {
  //  int newValue = position;
  //  int oldValue = captureButtonPositionPreference.get();
  //
  //  if (newValue != oldValue) {
  //    Log.d(TAG, String.format("Updating 'capture button position' preference to %s", newValue));
  //    captureButtonPositionPreference.set(newValue);
  //  }
  //}

  @OnClick(R.id.prefs_button)
  void onPrefsClicked(View v) {
    if (prefsLayout.getVisibility() == View.INVISIBLE) {
      prefsButton.animate()
          .translationYBy(prefsButton.getHeight() + prefsLayoutMarginTop)
          .setDuration(150)
          .setInterpolator(new AccelerateInterpolator(2.0f))
          .setListener(new AnimatorEndListener() {
            @Override public void onAnimationEnd(Animator animation) {
              prefsButton.setImageDrawable(null);
              layoutExpanAnimation();
            }
          });
    } else {

      prefsLayout.setVisibility(View.INVISIBLE); //animation needed

      prefsButton.animate()
          .translationYBy((prefsButton.getHeight() + prefsLayoutMarginTop) * -1)
          .setDuration(150)
          .setInterpolator(new DecelerateInterpolator(2.0f))
          .setListener(new AnimatorEndListener() {
            @Override public void onAnimationEnd(Animator animation) {
              prefsButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_colorize_white_24dp, null));
            }
          });
    }
  }

  private void layoutExpanAnimation() {
    int x = prefsLayout.getWidth();
    int y = 0;
    float maxRadius = (float) Math.hypot(prefsLayout.getWidth(), prefsLayout.getHeight());

    Animator animator = ViewAnimationUtils.createCircularReveal(prefsLayout, x, y, 200, maxRadius);
    prefsLayout.setVisibility(View.VISIBLE);
    animator.setInterpolator(new AccelerateDecelerateInterpolator());
    animator.start();
  }

  @OnClick(R.id.start_capture)
  void onStartCaptureClicked() {
    Intent mediaCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
    startActivityForResult(mediaCaptureIntent, CREATE_SCREEN_CAPTURE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CREATE_SCREEN_CAPTURE) {
      if (resultCode == Activity.RESULT_OK) {
        Intent intent = KoloroService.intent(this, resultCode, data);
        startService(intent);
      } else {
        Log.d(TAG, "Couldn't get permission to screen capture");
        //show message to user
      }
    }
  }
}
