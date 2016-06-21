package com.insuleto.koloro.ui.views;

import android.net.Uri;

public interface ColorPickerView {

  void displayCaptureImage(Uri imageUri);
  void updateColorList();

}
