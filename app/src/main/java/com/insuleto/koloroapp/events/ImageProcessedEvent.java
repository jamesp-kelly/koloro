package com.insuleto.koloroapp.events;

import android.net.Uri;
import android.widget.Toast;

public class ImageProcessedEvent {
  private boolean success;
  private Uri imageUri;
  private Toast captureToast;
  private Throwable throwable;

  public ImageProcessedEvent(boolean success, Uri imageUri, Toast captureToast) {
    this.success = success;
    this.imageUri = imageUri;
    this.captureToast = captureToast;
  }

  public ImageProcessedEvent(boolean success, Uri imageUri, Toast captureToast, Throwable throwable) {
    this.success = success;
    this.imageUri = imageUri;
    this.captureToast = captureToast;
    this.throwable = throwable;
  }

  public boolean isSuccess() {
    return success;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public Uri getImageUri() {
    return imageUri;
  }

  public Toast getCaptureToast() {
    return captureToast;
  }
}
