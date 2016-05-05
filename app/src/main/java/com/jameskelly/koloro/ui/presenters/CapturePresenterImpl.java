package com.jameskelly.koloro.ui.presenters;

import com.jameskelly.koloro.ui.views.CaptureView;

public class CapturePresenterImpl implements CapturePresenter {

  private CaptureView view;

  @Override public void bindView(CaptureView view) {
    this.view = view;
  }

  @Override public void startScreenCapture() {
    
  }
}
