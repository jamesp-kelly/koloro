package com.jameskelly.koloro.ui.presenters;

import com.jameskelly.koloro.model.KoloroObj;
import com.jameskelly.koloro.ui.views.KoloroView;
import java.util.List;

public interface KoloroPresenter {
  void onStart();
  void onStop();
  void setupRealm();
  void bindView(KoloroView view);
  void unbindView(KoloroView view);
  List<KoloroObj> getAllKoloroObjects();
  int getContrastingTextColor(int backgroundColor);
  void saveNote(KoloroObj koloroObj, String inputValue);
}
