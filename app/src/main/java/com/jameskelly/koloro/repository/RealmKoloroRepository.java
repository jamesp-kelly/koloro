package com.jameskelly.koloro.repository;

import android.content.Context;
import com.jameskelly.koloro.model.KoloroObj;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmKoloroRepository implements KoloroRepository {

  private static final String TAG = RealmKoloroRepository.class.getSimpleName();
  private static final int MAX_SIZE = 10;

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
    koloroObj.setHexString(hexstring);
    koloroObj.setSavedTimeStamp(System.currentTimeMillis());

    RealmResults<KoloroObj> koloroObjs = realm.where(KoloroObj.class).findAll().sort(KoloroObj.savedTimeStampField,
        Sort.DESCENDING);

    if (koloroObjs.size() > MAX_SIZE) {
      koloroObjs.deleteLastFromRealm();
    }

    realm.commitTransaction();

    return koloroObj;
  }
}
