package com.insuleto.koloroapp.ui.presenters;

import com.insuleto.koloroapp.repository.KoloroRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class PresenterModule {

  @Provides @Singleton
  KoloroPresenter provideKoloroPresenter(KoloroRepository repository) {
    return new KoloroPresenterImpl(repository);
  }

  @Provides @Singleton
  ColorPickerPresenter provideColorPickPresenter(KoloroRepository repository) {
    return new ColorPickPresenterImpl(repository);
  }
}
