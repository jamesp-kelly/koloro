package com.jameskelly.koloro.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import butterknife.ButterKnife;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.ui.presenters.CapturePresenter;
import com.jameskelly.koloro.ui.views.CaptureView;
import javax.inject.Inject;

public class CaptureActivity extends BaseActivity implements CaptureView {

  @Inject CapturePresenter capturePresenter;

  public static Intent intent(Context context) {
    return new Intent(context, CaptureActivity.class);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_capture);
    KoloroApplication.get(this).applicationComponent().inject(this);
    ButterKnife.bind(this);
    capturePresenter.bindView(this);

    displayCaptureOverLay();

    //presenter should get MediaProjectionManager and fire screen capture intent
    capturePresenter.startScreenCapture();
  }

  @Override public void displayCaptureOverLay() {

  }
}
