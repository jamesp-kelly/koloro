package com.jameskelly.koloro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.preferences.BooleanPreference;
import com.jameskelly.koloro.preferences.PreferencesModule;
import com.jameskelly.koloro.ui.views.KoloroView;
import javax.inject.Inject;
import javax.inject.Named;

public class KoloroActivity extends BaseActivity implements KoloroView {

  @Inject @Named(PreferencesModule.SHOW_NOTIFICATION_KEY)
  BooleanPreference showNotificationPreference;
  @Inject @Named(PreferencesModule.CAPTURE_BUTTON_VISIBLE_KEY)
  BooleanPreference captureButtonVisiblePreference;

  @BindView(R.id.switch_show_notification) Switch showNotificationSwitch;
  @BindView(R.id.switch_capture_button_visible) Switch captureButtonVisibleSwitch;
  @BindView(R.id.start_capture) Button startCaptureButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_koloro);
    KoloroApplication.get(this).applicationComponent().inject(this);
    ButterKnife.bind(this);

    bindSharedPreferences();
  }

  @Override public void bindSharedPreferences() {
    showNotificationSwitch.setChecked(showNotificationPreference.get());
    captureButtonVisibleSwitch.setChecked(captureButtonVisiblePreference.get());
  }

  @OnCheckedChanged(R.id.switch_show_notification)
  void onShowNotificationChanged() {
    boolean newValue = showNotificationSwitch.isChecked();
    boolean oldValue = showNotificationPreference.get();

    if (newValue != oldValue) {
      Log.d(TAG, String.format("Updating 'show notification' preference to %s", newValue));
      showNotificationPreference.set(newValue);
    }
  }

  @OnCheckedChanged(R.id.switch_capture_button_visible)
  void onCaptureButtonChanged() {
    boolean newValue = captureButtonVisibleSwitch.isChecked();
    boolean oldValue = captureButtonVisiblePreference.get();

    if (newValue != oldValue) {
      Log.d(TAG, String.format("Updating 'capture button visible' preference to %s", newValue));
      captureButtonVisiblePreference.set(newValue);
    }
  }

  @OnClick(R.id.start_capture)
  void onStartCaptureClicked() {
    Intent intent = CaptureActivity.intent(this);
    startActivity(intent);
  }
}
