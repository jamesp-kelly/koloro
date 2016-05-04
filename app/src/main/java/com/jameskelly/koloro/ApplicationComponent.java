package com.jameskelly.koloro;

import com.jameskelly.koloro.ui.KoloroActivity;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

  void inject(KoloroApplication koloroApplication);
  void inject(KoloroActivity koloroActivity);
}
