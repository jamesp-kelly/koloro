package com.jameskelly.koloro;

import android.app.Application;
import android.content.Context;
import android.media.projection.MediaProjectionManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class ApplicationModule {

  private final Application application;

  public ApplicationModule(Application application) {
    this.application = application;
  }

  @Provides @Singleton
  public Application provideKoloroApplication() {
    return application;
  }

  @Provides @Singleton
  public MediaProjectionManager provideMediaProjectionManager() {
    return (MediaProjectionManager) application.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
  }

  @Provides @Singleton
  public WindowManager provideWindowManager() {
    return (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
  }

  @Provides @Singleton
  public LayoutInflater provideLayoutInflater() {
    return (LayoutInflater) application.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }
}
