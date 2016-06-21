package com.insuleto.koloro;

import com.insuleto.koloro.preferences.PreferencesModule;
import com.insuleto.koloro.service.KoloroService;
import com.insuleto.koloro.ui.ColorPickActivity;
import com.insuleto.koloro.ui.KoloroActivity;
import com.insuleto.koloro.ui.PreferenceFragment;
import com.insuleto.koloro.ui.QuickLaunchActivity;
import com.insuleto.koloro.ui.presenters.PresenterModule;
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
