package com.insuleto.koloro.repository;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.insuleto.koloro.model.KoloroObj;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmKoloroRepository implements KoloroRepository {

  private static final String TAG = RealmKoloroRepository.class.getSimpleName();
  private static final int MAX_SIZE = 50;

  private Context context;
  private Realm realm;
  private RealmConfiguration realmConfiguration;

  public RealmKoloroRepository(Context context) {
    this.context = context;
  }

  @Override public void setupConnection() {
    realmConfiguration = new RealmConfiguration.Builder(context).build();
    realm = Realm.getInstance(realmConfiguration);
  }

  @Override public void closeConnection() {
    if (realm != null && !realm.isClosed()) {
      realm.close();
    }
  }

  @Override public KoloroObj getKoloroObj(int koloroObjid) {
    return realm.where(KoloroObj.class).equalTo(KoloroObj.idField, koloroObjid).findFirst();
  }

  @Override public RealmResults<KoloroObj> getAllKoloroObjs() {
    return realm.where(KoloroObj.class).findAll().sort(KoloroObj.savedTimeStampField,
        Sort.DESCENDING);
  }

  @Override public KoloroObj createKoloroObj(int colorInt, String hexstring) {
    realm.beginTransaction();

    KoloroObj koloroObj = realm.createObject(KoloroObj.class);
    int key;
    try {
      key = realm.where(KoloroObj.class).max(KoloroObj.idField).intValue() + 1;
    } catch (ArrayIndexOutOfBoundsException ex) {
      key = 0;
    }
    koloroObj.setId(key);
    koloroObj.setColorInt(colorInt);
    koloroObj.setRed(Color.red(colorInt));
    koloroObj.setBlue(Color.blue(colorInt));
    koloroObj.setGreen(Color.green(colorInt));
    koloroObj.setHexString(hexstring);
    koloroObj.setSavedTimeStamp(System.currentTimeMillis());
    koloroObj.setNote("");

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
    realm.beginTransaction();
    koloroObj.setNote(note);
    realm.commitTransaction();
  }

  @Override public void deleteAllKoloroObjects() {
    realm.beginTransaction();
    realm.deleteAll();
    realm.commitTransaction();
  }
}
