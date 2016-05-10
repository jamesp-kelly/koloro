package com.jameskelly.koloro.ui.views;

import android.view.WindowManager;

public interface CaptureView {
  WindowManager.LayoutParams setupCaptureWindowLayoutParams();
  void displayCaptureOverlay();
  void hideCaptureOverlay();
}
