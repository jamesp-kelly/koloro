package com.jameskelly.koloro.ui.presenters;

import com.jameskelly.koloro.events.ImageProcessedEvent;
import com.jameskelly.koloro.model.KoloroObj;
import com.jameskelly.koloro.ui.views.ColorPickerView;
import java.util.List;

public interface ColorPickerPresenter {

  void onStart();
  void onStop();
  void setupRealm();
  void bindView(ColorPickerView view);
  void unbindView(ColorPickerView view);
  void onImageProcessedEventReceived(ImageProcessedEvent event);
  String generateHexColor(int color);
  int getContrastingTextColor(int backgroundColor);
  void saveColor(int currentlySelectedColor, String currentlySelectedColorHex);
  List<KoloroObj> getAllKoloroObjects();
  void saveNote(KoloroObj koloroObj, String inputValue);
}
