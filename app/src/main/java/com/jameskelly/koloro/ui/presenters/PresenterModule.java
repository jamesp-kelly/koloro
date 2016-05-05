package com.jameskelly.koloro.ui.presenters;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class PresenterModule {
  //private final CaptureView captureView;
  //
  //public PresenterModule(CaptureView captureView) {
  //  this.captureView = captureView;
  //}
  //
  //@Provides @Singleton
  //CaptureView provideCaptureView() {
  //  return captureView;
  //}
  //
  //@Provides @Singleton
  //CapturePresenter provideCapturePresenter() {
  //  return new CapturePresenterImpl(captureView);
  //}

  @Provides @Singleton
  CapturePresenter provideCapturePresenter() {
    return new CapturePresenterImpl();
  }
}
