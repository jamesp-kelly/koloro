package com.insuleto.koloroapp.ui.presenters;

import android.content.Intent;
import com.insuleto.koloroapp.ui.views.CaptureView;

public interface CapturePresenter {
  void startScreenCapture(Intent data);
  void bindView(CaptureView view);
}
