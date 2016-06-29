package com.insuleto.koloroapp;

import com.insuleto.koloroapp.preferences.PreferencesModule;
import com.insuleto.koloroapp.service.KoloroService;
import com.insuleto.koloroapp.ui.ColorPickActivity;
import com.insuleto.koloroapp.ui.KoloroActivity;
import com.insuleto.koloroapp.ui.PreferenceFragment;
import com.insuleto.koloroapp.ui.QuickLaunchActivity;
import com.insuleto.koloroapp.ui.presenters.PresenterModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    ApplicationModule.class,
    PreferencesModule.class,
    PresenterModule.class
})
public interface ApplicationComponent {

  void inject(KoloroApplication koloroApplication);
  void inject(KoloroActivity koloroActivity);
  void inject(ColorPickActivity colorPickActivity);
  void inject(KoloroService koloroService);
  void inject(ScreenCaptureManager screenCaptureManager);
  void inject(PreferenceFragment preferenceFragment);
  void inject(QuickLaunchActivity quickLaunchActivity);
}
