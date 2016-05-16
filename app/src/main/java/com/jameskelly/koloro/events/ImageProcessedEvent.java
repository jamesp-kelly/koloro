package com.jameskelly.koloro.events;

import android.net.Uri;

public class ImageProcessedEvent {
  private boolean success;
  private Uri imageUri;
  private Throwable throwable;

  public ImageProcessedEvent(boolean success, Uri imageUri) {
    this.success = success;
    this.imageUri = imageUri;
  }

  public ImageProcessedEvent(boolean success, Uri imageUri, Throwable throwable) {
    this.success = success;
    this.imageUri = imageUri;
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
}
