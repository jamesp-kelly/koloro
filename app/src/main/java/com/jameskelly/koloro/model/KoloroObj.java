package com.jameskelly.koloro.model;

import io.realm.RealmObject;

public class KoloroObj extends RealmObject {

  private long id;
  public static final String idField = "id";

  private String hexString;
  public static final String hexStringField = "hexString";

  private int colorInt;
  public static final String colorIntField = "colorInt";

  private long savedTimeStamp;
  public static final String savedTimeStampField = "savedTimeStamp";

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getHexString() {
    return hexString;
  }

  public void setHexString(String hexString) {
    this.hexString = hexString;
  }

  public int getColorInt() {
    return colorInt;
  }

  public void setColorInt(int colorInt) {
    this.colorInt = colorInt;
  }

  public long getSavedTimeStamp() {
    return savedTimeStamp;
  }

  public void setSavedTimeStamp(long savedTimeStamp) {
    this.savedTimeStamp = savedTimeStamp;
  }
}
