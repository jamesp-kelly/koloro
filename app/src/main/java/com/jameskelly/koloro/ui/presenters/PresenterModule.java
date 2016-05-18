package com.jameskelly.koloro.ui.presenters;

import com.jameskelly.koloro.repository.KoloroRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class PresenterModule {

  @Provides @Singleton
  ColorPickerPresenter provideColorPickPresenter(KoloroRepository repository) {
    return new ColorPickPresenterImpl(repository);
  }
}
