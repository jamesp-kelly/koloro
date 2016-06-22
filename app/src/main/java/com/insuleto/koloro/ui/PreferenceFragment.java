package com.insuleto.koloro.ui;

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
import butterknife.OnItemSelected;
import butterknife.OnTouch;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.insuleto.koloro.KoloroApplication;
import com.insuleto.koloro.R;
import com.insuleto.koloro.preferences.BooleanPreference;
import com.insuleto.koloro.preferences.IntPreference;
import com.insuleto.koloro.preferences.PreferencesModule;
import com.insuleto.koloro.preferences.SimpleSpinnerAdapter;
import com.insuleto.koloro.util.FirebaseEvents;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class PreferenceFragment extends Fragment {

  private static final String TAG = PreferenceFragment.class.getSimpleName();
  private SimpleSpinnerAdapter captureButtonPositionAdapter, colorFormatAdapter;
  private boolean isPremium = false;
  Toast toast;

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

  @BindView(R.id.spinner_capture_button_position) Spinner captureButtonPositionSpinner;
  @BindView(R.id.switch_vibration) Switch vibrationSwitch;
  @BindView(R.id.switch_store_captures) Switch storeCapturesSwitch;
  @BindView(R.id.switch_quick_launch) Switch quickLaunchSwitch;
  @BindView(R.id.spinner_color_format) Spinner colorFormatSpinner;
  @BindView(R.id.quick_launch_label) TextView quickLaunchText;


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

    captureButtonPositionAdapter = new SimpleSpinnerAdapter(getActivity(), buttonPositionValues());
    captureButtonPositionSpinner.setAdapter(captureButtonPositionAdapter);
    captureButtonPositionSpinner.setSelection(captureButtonPositionPreference.get());
    vibrationSwitch.setChecked(vibrationPreference.get());
    storeCapturesSwitch.setChecked(galleryPreference.get());
    quickLaunchSwitch.setChecked(quickLaunchPreference.get());
    colorFormatAdapter = new SimpleSpinnerAdapter(getActivity(), colorFormatValues());
    colorFormatSpinner.setAdapter(colorFormatAdapter);
    colorFormatSpinner.setSelection(colorFormatPreference.get());

    toast = Toast.makeText(getActivity(), "TEsting", Toast.LENGTH_SHORT);
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
    toast.cancel();
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
  void onQuickLaunchChanged() {
    boolean newValue = quickLaunchSwitch.isChecked();
    boolean oldValue = quickLaunchPreference.get();

    if (newValue != oldValue) {
      Log.d(TAG, String.format("Updating 'quick launch' preference to %s", newValue));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Quick Launch");
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(newValue));
      firebaseAnalytics.logEvent(FirebaseEvents.PREFERENCE_CHANGED, bundle);
      quickLaunchPreference.set(newValue);

      ((PreferenceChangeListener)getActivity()).boolValueChanged(PreferencesModule.QUICK_LAUNCH_KEY, newValue);
    }
  }

  @OnTouch(R.id.quick_launch_overlay)
  boolean onQuickLaunchTouched(){
    if (!isPremium) {
      toast.show();
      return true;
    } else {
      return false;
    }
  }

  public interface PreferenceChangeListener {
    void intValueChanged(String preferenceKey, int newValue);
    void boolValueChanged(String preferenceKey, boolean newValue);
  }

  public void updateBillingUI(boolean premiumEnabled) {
    isPremium = premiumEnabled;
    if (!isPremium) {

      disablePreference(PreferencesModule.QUICK_LAUNCH_KEY);
      //quickLaunchSwitch.setChecked(isPremium);
      //quickLaunchSwitch.setEnabled(isPremium);

      //none of thise works. set disabled and intercept the touch if premium = false. change drawble manually to look disabled

      //enable overlays
    }
  }

  private void disablePreference(String preferenceKey) {
    if (preferenceKey.equals(PreferencesModule.QUICK_LAUNCH_KEY)) {
      quickLaunchSwitch.setChecked(false);
      //quickLaunchText.setTextColor(R.color.colorAccent);
      quickLaunchText.setEnabled(false);
    }
  }
}
