package com.jameskelly.koloro;

import android.content.Context;

public class ScreenCaptureManager {

  private final Context context;
  private final ImageCaptureListener imageCaptureListener;

  public ScreenCaptureManager(Context context, ImageCaptureListener imageCaptureListener) {
    this.context = context;
    this.imageCaptureListener = imageCaptureListener;
    KoloroApplication.get(context).applicationComponent().inject(this);
  }

  public void captureScreen() {



    //imageCaptureListener.onImageCaptured();
  }

  public interface ImageCaptureListener {
    void onImageCaptured(String captureUri);
    void onImageCaptureError();
  }
}
