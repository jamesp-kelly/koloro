package com.jameskelly.koloro;

import com.jameskelly.koloro.preferences.PreferencesModule;
import com.jameskelly.koloro.service.KoloroService;
import com.jameskelly.koloro.ui.ColorPickActivity;
import com.jameskelly.koloro.ui.KoloroActivity;
import com.jameskelly.koloro.ui.presenters.PresenterModule;
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
}
