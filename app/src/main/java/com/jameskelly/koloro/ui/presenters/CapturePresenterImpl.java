package com.jameskelly.koloro.ui.presenters;

import android.content.Intent;
import com.jameskelly.koloro.ui.views.CaptureView;

public class CapturePresenterImpl implements CapturePresenter {

  private CaptureView view;

  @Override public void bindView(CaptureView view) {
    this.view = view;
  }

  @Override public void startScreenCapture(Intent data) {

  }
}