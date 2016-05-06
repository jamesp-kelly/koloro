package com.jameskelly.koloro;

import android.content.Context;
import android.os.Environment;
import java.io.File;

public class ScreenCaptureManager {

  private final Context context;
  private final ImageCaptureListener imageCaptureListener;

  public ScreenCaptureManager(Context context, ImageCaptureListener imageCaptureListener) {
    this.context = context;
    this.imageCaptureListener = imageCaptureListener;
    KoloroApplication.get(context).applicationComponent().inject(this);
  }

  public void captureScreen() {

    File galleryRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    File koloroDir = new File(galleryRoot, "Koloro");


    imageCaptureListener.onImageCaptured("farts");
  }

  public interface ImageCaptureListener {
    void onImageCaptured(String captureUri);
    void onImageCaptureError();
  }
}
