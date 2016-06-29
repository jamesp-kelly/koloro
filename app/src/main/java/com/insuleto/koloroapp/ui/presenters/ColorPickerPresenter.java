package com.insuleto.koloroapp.ui.presenters;

import com.insuleto.koloroapp.events.ImageProcessedEvent;
import com.insuleto.koloroapp.model.KoloroObj;
import com.insuleto.koloroapp.model.RgbColor;
import com.insuleto.koloroapp.ui.views.ColorPickerView;
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
