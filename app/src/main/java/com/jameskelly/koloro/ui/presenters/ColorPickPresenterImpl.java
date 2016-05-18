package com.jameskelly.koloro.ui.presenters;

import android.graphics.Color;
import com.jameskelly.koloro.events.ImageProcessedEvent;
import com.jameskelly.koloro.repository.KoloroRepository;
import com.jameskelly.koloro.ui.views.ColorPickerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ColorPickPresenterImpl implements ColorPickerPresenter {

  private KoloroRepository repository;
  private ColorPickerView view;

  public ColorPickPresenterImpl(KoloroRepository repository) {
    this.repository = repository;
  }

  @Override public void bindView(ColorPickerView view) {
    if (this.view != null) {
      throw new IllegalStateException("Previous view wasn't unbounded");
    }
    this.view = view;
  }

  @Override public void unbindView(ColorPickerView view) {
    if (this.view != view) {
      this.view = null;
    } else {
      throw new IllegalStateException("View wasn't bound. Cannot unbind");
    }
  }

  @Override public void onStart() {
    EventBus.getDefault().register(this);
    repository.setupConnection();
  }

  @Override public void onStop() {
    EventBus.getDefault().unregister(this);
    repository.closeConnection();
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  @Override public void onImageProcessedEventReceived(ImageProcessedEvent event) {
    if (event.isSuccess() && event.getImageUri() != null) {
      view.displayCaptureImage(event.getImageUri());
    }
  }

  @Override public String generateHexColor(int color) {
    int r = Color.red(color);
    int g = Color.green(color);
    int b = Color.blue(color);

    return String.format("#%02x%02x%02x", r, g, b);
  }

  @Override public int getContrastingTextColor(int backgroundColor) {
    int resultColorValue = 0;

    int r = Color.red(backgroundColor);
    int g = Color.green(backgroundColor);
    int b = Color.blue(backgroundColor);

    double backgroundBrightness = 1 - (0.299 * r + 0.587 * g + 0.114 * b)/255;

    if (backgroundBrightness < 0.5) {
      //bright color, use black
      resultColorValue = 0;
    } else {
      resultColorValue = 255;
    }

    return Color.argb(255, resultColorValue, resultColorValue, resultColorValue);
  }

  @Override public void saveColor(int currentlySelectedColor, String currentlySelectedColorHex) {
    repository.createKoloroObj(currentlySelectedColor, currentlySelectedColorHex);
  }
}
