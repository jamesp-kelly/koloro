package com.insuleto.koloro.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.github.paolorotolo.appintro.AppIntro2;
import com.insuleto.koloro.ui.layouts.HelpSlide1;
import com.insuleto.koloro.ui.layouts.HelpSlide2;
import com.insuleto.koloro.ui.layouts.HelpSlide3;
import com.insuleto.koloro.ui.layouts.HelpSlide4;

public class HelpActivity extends AppIntro2 {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addSlide(new HelpSlide1());
    addSlide(new HelpSlide2());
    addSlide(new HelpSlide3());
    addSlide(new HelpSlide4());


    skipButtonEnabled = false;
  }

  @Override public void onDonePressed(Fragment currentFragment) {
    finish();
  }
}
