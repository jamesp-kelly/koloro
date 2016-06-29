package com.insuleto.koloroapp.model;

import io.realm.RealmObject;

public class KoloroObj extends RealmObject {

  public static final String RGB_FORMAT = "R:%s G:%s B:%s";

  private long id;
  public static final String idField = "id";

  private String hexString;
  public static final String hexStringField = "hexString";

  private int colorInt;
  public static final String colorIntField = "colorInt";

  private long savedTimeStamp;
  public static final String savedTimeStampField = "savedTimeStamp";

  private String note;
  public static final String noteField = "noteField";

  private int red;
  public static final String redField = "redField";

  private int blue;
  public static final String blueField = "blueField";

  private int green;
  public static final String greenField = "greenField";

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getHexString() {
    return hexString;
  }

  public String getRgbString() {
    return String.format(RGB_FORMAT, this.red, this.green, this.blue);
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

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public int getBlue() {
    return blue;
  }

  public void setBlue(int blue) {
    this.blue = blue;
  }

  public int getGreen() {
    return green;
  }

  public void setGreen(int green) {
    this.green = green;
  }

  public int getRed() {
    return red;
  }

  public void setRed(int red) {
    this.red = red;
  }
}
