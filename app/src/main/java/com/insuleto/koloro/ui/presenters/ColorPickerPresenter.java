package com.insuleto.koloro.ui.presenters;

import com.insuleto.koloro.events.ImageProcessedEvent;
import com.insuleto.koloro.model.KoloroObj;
import com.insuleto.koloro.model.RgbColor;
import com.insuleto.koloro.ui.views.ColorPickerView;
import java.util.List;

public interface ColorPickerPresenter {

  void onStart();
  void onStop();
  void setupRealm();
  void bindView(ColorPickerView view);
  void unbindView(ColorPickerView view);
  void onImageProcessedEventReceived(ImageProcessedEvent event);
  String generateHexColor(int color);
  RgbColor generateRgbColor(int color);
  int getContrastingTextColor(int backgroundColor);
  void saveColor(int currentlySelectedColor, String currentlySelectedColorHex);
  List<KoloroObj> getAllKoloroObjects();
  void removeAllKoloroObjects();
  void saveNote(KoloroObj koloroObj, String inputValue);
  void removeStoredScreenShot();
}
