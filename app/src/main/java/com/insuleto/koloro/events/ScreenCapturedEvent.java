package com.insuleto.koloro.events;

public class ScreenCapturedEvent {
  private boolean success;
  private Throwable throwable;

  public ScreenCapturedEvent(boolean success) {
    this.success = success;
  }

  public ScreenCapturedEvent(boolean success, Throwable throwable) {
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
