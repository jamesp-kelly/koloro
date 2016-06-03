package com.jameskelly.koloro.ui.presenters;

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
    //koloroObjects.addChangeListener(() -> view.updateColorList());
    return koloroObjects;
  }
}
