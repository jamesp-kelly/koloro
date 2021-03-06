package com.insuleto.koloroapp;

import android.app.Application;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.projection.MediaProjectionManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.WindowManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.insuleto.koloroapp.repository.KoloroRepository;
import com.insuleto.koloroapp.repository.RealmKoloroRepository;
import com.squareup.picasso.Picasso;
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
  public NotificationManager provideNotificationManager() {
    return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  @Provides @Singleton
  public ClipboardManager provideClipboardManager() {
    return (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
  }

  @Provides @Singleton
  public Vibrator providesVibrator() {
    return (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
  }

  @Provides @Singleton
  public LayoutInflater provideLayoutInflater() {
    return (LayoutInflater) application.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Provides @Singleton
  public Picasso providePicasso() {
    return new Picasso.Builder(application)
        .build();
  }

  @Provides @Singleton
  public KoloroRepository provideKoloroRepository() {
    return new RealmKoloroRepository();
  }

  @Provides @Singleton
  public FirebaseAnalytics provideFirebaseAnalytics() {
    return FirebaseAnalytics.getInstance(application.getApplicationContext());
  }
}
