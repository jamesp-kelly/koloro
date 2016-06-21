package com.insuleto.koloro.ui.presenters;

import android.content.Intent;
import com.insuleto.koloro.ui.views.CaptureView;

public interface CapturePresenter {
  void startScreenCapture(Intent data);
  void bindView(CaptureView view);
}
