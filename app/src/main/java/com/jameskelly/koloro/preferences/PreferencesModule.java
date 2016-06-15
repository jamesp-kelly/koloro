package com.jameskelly.koloro.preferences;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class PreferencesModule {

  public static final String CAPTURE_BUTTON_POSITION_KEY = "capture_button_position";
  private static final int DEFAULT_CAPTURE_BUTTON_POSITION = 0;
  public static final String STORE_CAPTURES_IN_GALLERY_KEY = "store_captures_in_gallery";
  private static final boolean DEFAULT_CAPTURES_IN_GALLEY = true;
  public static final String VIBRATION_KEY = "vibration";
  private static final boolean DEFAULT_VIBRATION = true;
  public static final String QUICK_LAUNCH_KEY = "quick_launch";
  private static final boolean DEFAULT_QUICK_LAUNCH = false;
  public static final String COLOR_FORMAT_KEY = "color_format";
  public static final int DEFAULT_COLOR_FORMAT = 0;

  @Provides @Singleton
  SharedPreferences provideSharedPreferences(Application app) {
    return PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
  }

  @Provides @Singleton @Named(CAPTURE_BUTTON_POSITION_KEY)
  IntPreference provideCaptureButtonPositionPreference(SharedPreferences preferences) {
    return new IntPreference(preferences, CAPTURE_BUTTON_POSITION_KEY, DEFAULT_CAPTURE_BUTTON_POSITION);
  }

  @Provides @Named(CAPTURE_BUTTON_POSITION_KEY)
  Integer provideCaptureButtonPosition(@Named(CAPTURE_BUTTON_POSITION_KEY) IntPreference intPreference) {
    return intPreference.get();
  }

  @Provides @Singleton @Named(STORE_CAPTURES_IN_GALLERY_KEY)
  BooleanPreference provideStoreCaptureInGalleryPreference(SharedPreferences preferences) {
    return new BooleanPreference(preferences, STORE_CAPTURES_IN_GALLERY_KEY,
        DEFAULT_CAPTURES_IN_GALLEY);
  }

  @Provides @Named(STORE_CAPTURES_IN_GALLERY_KEY)
  Boolean provideStoreCaptureInGallery(@Named(STORE_CAPTURES_IN_GALLERY_KEY) BooleanPreference booleanPreference) {
    return booleanPreference.get();
  }

  @Provides @Singleton @Named(VIBRATION_KEY)
  BooleanPreference provideVibrationPreference(SharedPreferences preferences) {
    return new BooleanPreference(preferences, VIBRATION_KEY, DEFAULT_VIBRATION);
  }

  @Provides @Named(VIBRATION_KEY)
  Boolean provideVibration(@Named(VIBRATION_KEY) BooleanPreference booleanPreference) {
    return booleanPreference.get();
  }

  @Provides @Singleton @Named(QUICK_LAUNCH_KEY)
  BooleanPreference provideQuickLaunchPreference(SharedPreferences preferences) {
    return new BooleanPreference(preferences, QUICK_LAUNCH_KEY, DEFAULT_QUICK_LAUNCH);
  }

  @Provides @Named(QUICK_LAUNCH_KEY)
  Boolean provideQuickLaunch(@Named(QUICK_LAUNCH_KEY) BooleanPreference booleanPreference) {
    return booleanPreference.get();
  }

}
