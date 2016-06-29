package com.insuleto.koloroapp.ui.presenters;

import com.insuleto.koloroapp.model.KoloroObj;
import com.insuleto.koloroapp.ui.views.KoloroView;
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
  boolean realmActive();
  void removeAllKoloroObjects();
}
