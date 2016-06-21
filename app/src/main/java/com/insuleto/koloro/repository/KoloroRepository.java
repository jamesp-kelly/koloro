package com.insuleto.koloro.repository;

import com.insuleto.koloro.model.KoloroObj;
import io.realm.RealmResults;

public interface KoloroRepository {
  KoloroObj getKoloroObj(int koloroObjid);
  RealmResults<KoloroObj> getAllKoloroObjs();

  KoloroObj createKoloroObj(int colorInt, String hexstring);
  void updateNote(KoloroObj koloroObj, String note);
  void setupConnection();
  void closeConnection();
}
