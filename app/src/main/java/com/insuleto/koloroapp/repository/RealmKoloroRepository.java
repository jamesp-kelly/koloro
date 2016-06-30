package com.insuleto.koloroapp.repository;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.insuleto.koloroapp.model.KoloroObj;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmKoloroRepository implements KoloroRepository {

  private static final String TAG = RealmKoloroRepository.class.getSimpleName();
  private static final int MAX_SIZE = 50;

  //private Context context;
  //private Realm realm;
  //private RealmConfiguration realmConfiguration;

  //public RealmKoloroRepository(Context context) {
  //  this.context = context;
  //}

  @Override public void setupConnection(Context context) {
    RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();
    Realm.setDefaultConfiguration(realmConfiguration);
  }

  @Override public void closeConnection() {
    Realm realm = Realm.getDefaultInstance();
    if (realm != null && !realm.isClosed()) {
      realm.close();
    }
  }

  @Override public KoloroObj getKoloroObj(int koloroObjid) {
    Realm realm = Realm.getDefaultInstance();
    return realm.where(KoloroObj.class).equalTo(KoloroObj.idField, koloroObjid).findFirst();
  }

  @Override public RealmResults<KoloroObj> getAllKoloroObjs() {
    Realm realm = Realm.getDefaultInstance();
    return realm.where(KoloroObj.class).findAll().sort(KoloroObj.savedTimeStampField,
        Sort.DESCENDING);
  }

  @Override public KoloroObj createKoloroObj(int colorInt, String hexstring) {
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();

    int key;
    try {
      key = realm.where(KoloroObj.class).max(KoloroObj.idField).intValue() + 1;
    } catch (ArrayIndexOutOfBoundsException ex) {
      key = 0;
    }

    KoloroObj koloroObj = new KoloroObj.Builder(key)
        .colorInt(colorInt)
        .colorValues(Color.red(colorInt), Color.green(colorInt), Color.blue(colorInt))
        .hexString(hexstring)
        .savedTimeStamp(System.currentTimeMillis())
        .build();

    RealmResults<KoloroObj> koloroObjs = realm.where(KoloroObj.class).findAll().sort(KoloroObj.savedTimeStampField,
        Sort.DESCENDING);

    Log.d(TAG, "Number of Koloro objs stored: " + koloroObjs.size());

    if (koloroObjs.size() > MAX_SIZE) {
      koloroObjs.deleteLastFromRealm();
      Log.d(TAG, "Deleting oldest koloro obj");
    }

    realm.commitTransaction();
    return koloroObj;
  }

  @Override public void updateNote(KoloroObj koloroObj, String note) {
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    koloroObj.setNote(note);
    realm.commitTransaction();
  }

  @Override public void deleteAllKoloroObjects() {
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    realm.deleteAll();
    realm.commitTransaction();
  }
}
