package com.insuleto.koloroapp.model;

import io.realm.Realm;
import io.realm.RealmObject;

public class KoloroObj extends RealmObject {

  public static class Builder {
    private long id;
    private String hexString;
    private int colorInt;
    private long savedTimeStamp;
    private String note;
    private int red;
    private int green;
    private int blue;

    private KoloroObj koloroObj;
    private Realm realm;

    public Builder(long id) {
      this.id = id;
    }

    public KoloroObj build() {
      realm = Realm.getDefaultInstance();
      koloroObj = realm.createObject(KoloroObj.class);
      return koloroObj.constructFromBuilder(this);
    }

    public Builder hexString(String hexString) {
      this.hexString = hexString;
      return this;
    }

    public Builder colorInt(int colorInt) {
      this.colorInt = colorInt;
      return this;
    }

    public Builder savedTimeStamp(long savedTimeStamp) {
      this.savedTimeStamp = savedTimeStamp;
      return this;
    }

    public Builder note(String note) {
      this.note = note;
      return this;
    }

    public Builder colorValues(int red, int green, int blue) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      return this;
    }
  }

  public static final String RGB_FORMAT = "R:%s G:%s B:%s";

  private long id;
  public static final String idField = "id";

  private String hexString;
  public static final String hexStringField = "hexString";

  private int colorInt;
  public static final String colorIntField = "colorInt";

  private long savedTimeStamp;
  public static final String savedTimeStampField = "savedTimeStamp";

  private String note; //non final. user can set note
  public static final String noteField = "noteField";

  private int red;
  public static final String redField = "redField";

  private int blue;
  public static final String blueField = "blueField";

  private int green;
  public static final String greenField = "greenField";

  public KoloroObj constructFromBuilder(Builder builder) {
    this.id = builder.id;
    this.hexString = builder.hexString;
    this.colorInt = builder.colorInt;
    this.savedTimeStamp = builder.savedTimeStamp;
    this.note = builder.note;
    this.red = builder.red;
    this.green = builder.green;
    this.blue = builder.blue;

    return this;
  }

  public long getId() {
    return id;
  }

  public String getHexString() {
    return hexString;
  }

  public String getRgbString() {
    return String.format(RGB_FORMAT, this.red, this.green, this.blue);
  }

  public int getColorInt() {
    return colorInt;
  }

  public long getSavedTimeStamp() {
    return savedTimeStamp;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public int getRed() {
    return red;
  }

  public int getGreen() {
    return green;
  }

  public int getBlue() {
    return blue;
  }

}
