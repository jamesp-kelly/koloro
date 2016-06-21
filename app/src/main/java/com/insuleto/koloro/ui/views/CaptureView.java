package com.insuleto.koloro.ui.views;

import android.view.WindowManager;

public interface CaptureView {
  WindowManager.LayoutParams layoutParams(int positionPref);
  void displayCaptureOverlay();
  void hideCaptureOverlay();
}
