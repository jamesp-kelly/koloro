package com.insuleto.koloroapp.ui.presenters;

import android.content.Intent;
import com.insuleto.koloroapp.ui.views.CaptureView;

public class CapturePresenterImpl implements CapturePresenter {

  private CaptureView view;

  @Override public void bindView(CaptureView view) {
    this.view = view;
  }

  @Override public void startScreenCapture(Intent data) {

  }
}
