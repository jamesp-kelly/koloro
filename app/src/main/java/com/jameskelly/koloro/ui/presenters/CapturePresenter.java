package com.jameskelly.koloro.ui.presenters;

import android.content.Intent;
import com.jameskelly.koloro.ui.views.CaptureView;

public interface CapturePresenter {
  void startScreenCapture(Intent data);
  void bindView(CaptureView view);
}
