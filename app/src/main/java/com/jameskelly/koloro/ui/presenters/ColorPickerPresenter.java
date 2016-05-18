package com.jameskelly.koloro.ui.presenters;

import com.jameskelly.koloro.events.ImageProcessedEvent;
import com.jameskelly.koloro.ui.views.ColorPickerView;

public interface ColorPickerPresenter {

  void onStart();
  void onStop();
  void bindView(ColorPickerView view);
  void unbindView(ColorPickerView view);
  void onImageProcessedEventReceived(ImageProcessedEvent event);
  String generateHexColor(int color);
  int getContrastingTextColor(int backgroundColor);
  void saveColor(int currentlySelectedColor, String currentlySelectedColorHex);
}
