package com.jameskelly.koloro.ui.presenters;

import android.graphics.Color;
import com.jameskelly.koloro.model.KoloroObj;
import com.jameskelly.koloro.repository.KoloroRepository;
import com.jameskelly.koloro.ui.views.KoloroView;
import io.realm.RealmResults;
import java.util.List;

public class KoloroPresenterImpl implements KoloroPresenter {

  private KoloroRepository repository;
  private KoloroView view;
  private RealmResults<KoloroObj> koloroObjects;

  public KoloroPresenterImpl(KoloroRepository repository) {
    this.repository = repository;
  }

  @Override public void onStart() {

  }

  @Override public void onStop() {
    repository.closeConnection();
  }

  @Override public void setupRealm() {
    repository.setupConnection();
  }

  @Override public void bindView(KoloroView view) {
    this.view = view;
  }

  @Override public void unbindView(KoloroView view) {
    this.view = null;
  }

  @Override public List<KoloroObj> getAllKoloroObjects() {
    koloroObjects = repository.getAllKoloroObjs();
    koloroObjects.addChangeListener(() -> view.updateColorList());
    return koloroObjects;
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

  @Override public void saveNote(KoloroObj koloroObj, String inputValue) {
    repository.updateNote(koloroObj, inputValue);
  }
}
