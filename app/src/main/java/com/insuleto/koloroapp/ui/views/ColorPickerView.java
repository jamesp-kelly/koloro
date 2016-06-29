package com.insuleto.koloroapp.ui.views;

import android.net.Uri;

public interface ColorPickerView {

  void displayCaptureImage(Uri imageUri);
  void updateColorList();

}
