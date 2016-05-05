package com.jameskelly.koloro.ui.presenters;

import com.jameskelly.koloro.ui.views.CaptureView;

public interface CapturePresenter {
  void startScreenCapture();
  void bindView(CaptureView view);
}
