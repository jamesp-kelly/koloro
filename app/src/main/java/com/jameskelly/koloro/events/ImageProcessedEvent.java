package com.jameskelly.koloro.events;

public class ImageProcessedEvent {
  private boolean success;
  private Throwable throwable;

  public ImageProcessedEvent(boolean success) {
    this.success = success;
  }

  public ImageProcessedEvent(boolean success, Throwable throwable) {
    this.success = success;
    this.throwable = throwable;
  }

  public boolean isSuccess() {
    return success;
  }

  public Throwable getThrowable() {
    return throwable;
  }
}
