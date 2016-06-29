package com.insuleto.koloroapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.insuleto.koloroapp.KoloroApplication;
import com.insuleto.koloroapp.R;
import com.insuleto.koloroapp.preferences.BooleanPreference;
import com.insuleto.koloroapp.preferences.IntPreference;
import com.insuleto.koloroapp.preferences.PreferencesModule;
import com.insuleto.koloroapp.preferences.SimpleSpinnerAdapter;
import com.insuleto.koloroapp.util.FirebaseEvents;
import it.sephiroth.android.library.tooltip.Tooltip;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class PreferenceFragment extends Fragment {

  private static final String TAG = PreferenceFragment.class.getSimpleName();
  //private boolean isPremium = false;

  @Inject FirebaseAnalytics firebaseAnalytics;
  @Inject @Named(PreferencesModule.CAPTURE_BUTTON_POSITION_KEY)
  IntPreference captureButtonPositionPreference;
  @Inject @Named(PreferencesModule.VIBRATION_KEY)
  BooleanPreference vibrationPreference;
  @Inject @Named(PreferencesModule.QUICK_LAUNCH_KEY)
  BooleanPreference quickLaunchPreference;
  @Inject @Named(PreferencesModule.STORE_CAPTURES_IN_GALLERY_KEY)
  BooleanPreference galleryPreference;
  @Inject @Named(PreferencesModule.COLOR_FORMAT_KEY)
  IntPreference colorFormatPreference;
  @Inject @Named(PreferencesModule.ZOOM_ENABLED_KEY)
  BooleanPreference zoomEnabledPreference;

  @Inject @Named(PreferencesModule.PREMIUM_ENABLED_KEY) BooleanPreference premiumEnabledPref;


  @BindView(R.id.spinner_capture_button_position) Spinner captureButtonPositionSpinner;
  @BindView(R.id.switch_vibration) Switch vibrationSwitch;
  @BindView(R.id.switch_store_captures) Switch storeCapturesSwitch;
  @BindView(R.id.spinner_color_format) Spinner colorFormatSpinner;

  @BindView(R.id.switch_quick_launch) Switch quickLaunchSwitch;
  @BindView(R.id.quick_launch_label) TextView quickLaunchText;
  @BindView(R.id.switch_zoom_enabled) Switch zoomEnabledSwitch;
  @BindView(R.id.zoom_enabled_label) TextView zoomEnabledText;


  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    KoloroApplication.get(getActivity()).applicationComponent().inject(this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_preferences, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.bind(this, view);

    SimpleSpinnerAdapter captureButtonPositionAdapter = new SimpleSpinnerAdapter(getActivity(), buttonPositionValues());
    captureButtonPositionSpinner.setAdapter(captureButtonPositionAdapter);
    captureButtonPositionSpinner.setSelection(captureButtonPositionPreference.get());
    vibrationSwitch.setChecked(vibrationPreference.get());
    storeCapturesSwitch.setChecked(galleryPreference.get());

    SimpleSpinnerAdapter colorFormatAdapter = new SimpleSpinnerAdapter(getActivity(), colorFormatValues());
    colorFormatSpinner.setAdapter(colorFormatAdapter);
    colorFormatSpinner.setSelection(colorFormatPreference.get());

    zoomEnabledSwitch.setChecked(premiumEnabledPref.get() && zoomEnabledPreference.get());
    quickLaunchSwitch.setChecked(premiumEnabledPref.get() && quickLaunchPreference.get());
  }

  private List<String> buttonPositionValues() {
    String[] stringArray = getResources().getStringArray(R.array.capture_button_position_array);
    return Arrays.asList(stringArray);
  }

  private List<String> colorFormatValues() {
    String[] stringArray = getResources().getStringArray(R.array.color_format_array);
    return Arrays.asList(stringArray);
  }

  @OnItemSelected(R.id.spinner_capture_button_position)
  void onCapturePositionChanged(int position) {
    int newValue = position;
    int oldValue = captureButtonPositionPreference.get();

    if (newValue != oldValue) {
      Log.d(TAG, String.format("Updating 'capture button position' preference to %s", newValue));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Capture button position");
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, buttonPositionValues().get(position));
      firebaseAnalytics.logEvent(FirebaseEvents.PREFERENCE_CHANGED, bundle);

      captureButtonPositionPreference.set(newValue);

      ((PreferenceChangeListener)getActivity()).intValueChanged(PreferencesModule.CAPTURE_BUTTON_POSITION_KEY, newValue);
    }
  }

  @OnItemSelected(R.id.spinner_color_format)
  void onColorFormatChanged(int position) {
    int newValue = position;
    int oldValue = colorFormatPreference.get();

    if (newValue != oldValue) {
      Log.d(TAG, String.format("Updating 'color format' preference to %s", newValue));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Color format");
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, colorFormatValues().get(position));
      firebaseAnalytics.logEvent(FirebaseEvents.PREFERENCE_CHANGED, bundle);

      colorFormatPreference.set(newValue);

      ((PreferenceChangeListener)getActivity()).intValueChanged(PreferencesModule.COLOR_FORMAT_KEY, newValue);
    }
  }

  @Override public void onPause() {
    super.onPause();
  }

  @OnCheckedChanged(R.id.switch_vibration)
  void onVibrationChanged() {
    boolean newValue = vibrationSwitch.isChecked();
    boolean oldValue = vibrationPreference.get();

    if (newValue != oldValue) {
      Log.d(TAG, String.format("Updating 'vibration' preference to %s", newValue));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Vibration");
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(newValue));
      firebaseAnalytics.logEvent(FirebaseEvents.PREFERENCE_CHANGED, bundle);
      vibrationPreference.set(newValue);

      ((PreferenceChangeListener)getActivity()).boolValueChanged(PreferencesModule.VIBRATION_KEY, newValue);
    }
  }

  @OnCheckedChanged(R.id.switch_store_captures)
  void onStoreCapturesChanged() {
    boolean newValue = storeCapturesSwitch.isChecked();
    boolean oldValue = galleryPreference.get();

    if (newValue != oldValue) {
      Log.d(TAG, String.format("Updating 'store captures' preference to %s", newValue));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Store captures");
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(newValue));
      firebaseAnalytics.logEvent(FirebaseEvents.PREFERENCE_CHANGED, bundle);
      galleryPreference.set(newValue);

      ((PreferenceChangeListener)getActivity()).boolValueChanged(PreferencesModule.STORE_CAPTURES_IN_GALLERY_KEY, newValue);
    }
  }

  @OnCheckedChanged(R.id.switch_quick_launch)
  void onQuickLaunchChanged(boolean checked) {
    if (!premiumEnabledPref.get()) {
      if (checked) {
        quickLaunchSwitch.setChecked(false);
        Toast.makeText(getActivity(), "Quick Launch is a Premium feature", Toast.LENGTH_SHORT).show();
      }
      return;
    }

    boolean oldValue = quickLaunchPreference.get();

    if (checked != oldValue) {
      Log.d(TAG, String.format("Updating 'quick launch' preference to %s", checked));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Quick Launch");
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(checked));
      firebaseAnalytics.logEvent(FirebaseEvents.PREFERENCE_CHANGED, bundle);
      quickLaunchPreference.set(checked);

      ((PreferenceChangeListener)getActivity()).boolValueChanged(PreferencesModule.QUICK_LAUNCH_KEY, checked);
    }
  }

  @OnCheckedChanged(R.id.switch_zoom_enabled)
  void zoomEnabledChanged(boolean checked) {
    if (!premiumEnabledPref.get()) {
      if (checked) {
        zoomEnabledSwitch.setChecked(false);
        Toast.makeText(getActivity(), "Zoom is aPremium feature", Toast.LENGTH_SHORT).show();
      }
      return;
    }

    boolean oldValue = zoomEnabledPreference.get();

    if (checked != oldValue) {
      Log.d(TAG, String.format("Updating 'zoom enabled' preference to %s", checked));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Zoom Enabled");
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(checked));
      firebaseAnalytics.logEvent(FirebaseEvents.PREFERENCE_CHANGED, bundle);
      zoomEnabledPreference.set(checked);

      ((PreferenceChangeListener)getActivity()).boolValueChanged(PreferencesModule.ZOOM_ENABLED_KEY, checked);
    }
  }

  @OnClick(R.id.tooltip_quick_launch)
  void onClickTipQuickLaunch(View v) {
    displayTooltip(v, R.string.tooltip_quick_launch);
  }

  @OnClick(R.id.tooltip_zoom_enabled)
  void onClickZoomEnabled(View v) {
    displayTooltip(v, R.string.tooltip_enable_zoom);
  }

  @OnClick(R.id.tooltip_retain_screenshots)
  void onClickRetainScreenshots(View v) {
    displayTooltip(v, R.string.tooltip_retain_screenshots);
  }

  @OnClick(R.id.tooltip_vibration)
  void onClickTooltipVibration(View v) {
    displayTooltip(v, R.string.tooltip_retain_screenshots);
  }


  private void displayTooltip(View view, int messageRes) {
    Tooltip.make(getActivity(),
        new Tooltip.Builder(101)
            .anchor(view, Tooltip.Gravity.TOP)
            .closePolicy(new Tooltip.ClosePolicy()
                .insidePolicy(true, false)
                .outsidePolicy(true, false), 3000)
            .activateDelay(800)
            .showDelay(300)
            .text(getText(messageRes))
            .withOverlay(false)
            .maxWidth(500)
            .withArrow(true)
            .build()).show();
  }

  public interface PreferenceChangeListener {
    void intValueChanged(String preferenceKey, int newValue);
    void boolValueChanged(String preferenceKey, boolean newValue);
  }

  public void updateBillingUI(boolean premiumEnabled) {
    //isPremium = premiumEnabled;
    updatePreference(PreferencesModule.QUICK_LAUNCH_KEY);
    updatePreference(PreferencesModule.ZOOM_ENABLED_KEY);
  }

  private void updatePreference(String preferenceKey) {
    boolean premiumEnabled = premiumEnabledPref.get();
    Log.i("KoloroActivity", "updatePreference: premiumEnabled: " + premiumEnabled);


    switch (preferenceKey) {
      case PreferencesModule.QUICK_LAUNCH_KEY: {
        quickLaunchSwitch.setChecked(premiumEnabled && quickLaunchPreference.get());
        quickLaunchText.setTextColor(getResources().getColor(
            premiumEnabled ? android.R.color.black : R.color.text_disabled));
        break;
      }
      case PreferencesModule.ZOOM_ENABLED_KEY: {
        zoomEnabledSwitch.setChecked(premiumEnabled && zoomEnabledPreference.get());
        Log.i("KoloroActivity", "zoom enabled setting to - " + (premiumEnabled && zoomEnabledPreference.get()));
        zoomEnabledText.setTextColor(getResources().getColor(
            premiumEnabled ? android.R.color.black :R.color.text_disabled));
        break;
      }
    }
  }
}
