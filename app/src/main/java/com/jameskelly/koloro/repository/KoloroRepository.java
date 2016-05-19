package com.jameskelly.koloro.repository;

import com.jameskelly.koloro.model.KoloroObj;
import io.realm.RealmResults;

public interface KoloroRepository {
  KoloroObj getKoloroObj(int koloroObjid);
  RealmResults<KoloroObj> getAllKoloroObjs();

  KoloroObj createKoloroObj(int colorInt, String hexstring);
  void setupConnection();
  void closeConnection();
}
