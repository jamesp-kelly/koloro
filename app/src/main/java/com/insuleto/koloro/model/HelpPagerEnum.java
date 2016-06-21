package com.insuleto.koloro.model;

import com.insuleto.koloro.R;

public enum HelpPagerEnum {

  INTRO(R.string.help_intro, R.drawable.youtube_screenshot),
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
