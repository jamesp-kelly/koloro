package com.insuleto.koloroapp.repository;

import android.content.Context;
import com.insuleto.koloroapp.model.KoloroObj;
import io.realm.RealmResults;

public interface KoloroRepository {
  KoloroObj getKoloroObj(int koloroObjid);
  RealmResults<KoloroObj> getAllKoloroObjs();

  KoloroObj createKoloroObj(int colorInt, String hexstring);
  void updateNote(KoloroObj koloroObj, String note);
  void deleteAllKoloroObjects();
  void setupConnection(Context context);
  void closeConnection();
}
