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
  public static final String SHOW_NOTIFICATION_KEY = "show_notification_key";
  private static final boolean DEFAULT_SHOW_NOTIFICATION = true;
  public static final String CAPTURE_BUTTON_VISIBLE_KEY = "capture_button_visible";
  private static final boolean DEFAULT_CAPTURE_BUTTON_VISIBLE = true;
  public static final String STORE_CAPTURES_IN_GALLERY_KEY = "store_captures_in_gallery";
  private static final boolean DEFAULT_CAPTURES_IN_GALLEY = true;
  public static final String MULTI_SHOT_KEY = "multi_shot";
  private static final boolean DEFAULT_MULTI_SHOT = false;
  public static final String CAPTURE_BUTTON_POSITION_KEY = "capture_button_position";
  private static final int DEFAULT_CAPTURE_BUTTON_POSITION = 0;

  @Provides @Singleton
  SharedPreferences provideSharedPreferences(Application app) {
    return PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
  }

  @Provides @Singleton @Named(SHOW_NOTIFICATION_KEY)
  BooleanPreference provideShowNotificationPreference(SharedPreferences preferences) {
    return new BooleanPreference(preferences, SHOW_NOTIFICATION_KEY, DEFAULT_SHOW_NOTIFICATION);
  }

  @Provides @Named(SHOW_NOTIFICATION_KEY)
  Boolean provideShowNotification(@Named(SHOW_NOTIFICATION_KEY) BooleanPreference booleanPreference) {
    return booleanPreference.get();
  }

  @Provides @Singleton @Named(CAPTURE_BUTTON_VISIBLE_KEY)
  BooleanPreference provideCaptureButtonVisiblePreference(SharedPreferences preferences) {
    return new BooleanPreference(preferences, CAPTURE_BUTTON_VISIBLE_KEY, DEFAULT_CAPTURE_BUTTON_VISIBLE);
  }

  @Provides @Named(CAPTURE_BUTTON_VISIBLE_KEY)
  Boolean provideCaptureButtonVisible(@Named(CAPTURE_BUTTON_VISIBLE_KEY) BooleanPreference booleanPreference) {
    return booleanPreference.get();
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

  //multi shot allows the user to take multiple shots rapidly and can choose which to inspect
  @Provides @Singleton @Named(MULTI_SHOT_KEY)
  BooleanPreference provideMultiShotPreference(SharedPreferences preferences) {
    return new BooleanPreference(preferences, MULTI_SHOT_KEY, DEFAULT_MULTI_SHOT);
  }

  @Provides @Named(MULTI_SHOT_KEY)
  Boolean provideMultiShot(@Named(MULTI_SHOT_KEY) BooleanPreference booleanPreference) {
    return booleanPreference.get();
  }

  @Provides @Singleton @Named(CAPTURE_BUTTON_POSITION_KEY)
  IntPreference provideCaptureButtonPositionPreference(SharedPreferences preferences) {
    return new IntPreference(preferences, CAPTURE_BUTTON_POSITION_KEY, DEFAULT_CAPTURE_BUTTON_POSITION);
  }

  @Provides @Named(CAPTURE_BUTTON_POSITION_KEY)
  Integer provideCaptureButtonPosition(@Named(CAPTURE_BUTTON_POSITION_KEY) IntPreference intPreference) {
    return intPreference.get();
  }

}
