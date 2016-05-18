package com.jameskelly.koloro.repository;

import com.jameskelly.koloro.model.KoloroObj;
import java.util.List;

public interface KoloroRepository {
  KoloroObj getKoloroObj(int koloroObjid);
  List<KoloroObj> getAllKoloroObjs();
  List<KoloroObj> getLatestKoloroObjs(int limit);

  KoloroObj createKoloroObj(int colorInt, String hexstring);
  void setupConnection();
  void closeConnection();
}
