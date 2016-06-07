package com.jameskelly.koloro.model;

import com.jameskelly.koloro.R;

public enum HelpPagerEnum {

  INTRO(R.string.help_intro, R.drawable.ic_palette_black_24dp),
  HOME(R.string.help_home, R.drawable.ic_palette_black_24dp),
  OVERLAY(R.string.help_overlay, R.drawable.ic_palette_black_24dp);

  private int stringRes;
  private int imageRes;

  HelpPagerEnum(int stringRes, int imageRes) {
    this.stringRes = stringRes;
    this.imageRes = imageRes;
  }

  public int getStringRes() {
    return stringRes;
  }

  public int getImageRes() {
    return imageRes;
  }
}
