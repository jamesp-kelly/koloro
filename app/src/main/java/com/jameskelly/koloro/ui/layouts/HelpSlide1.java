package com.jameskelly.koloro.ui.layouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.ui.HelpFragment;

public class HelpSlide1 extends HelpFragment {

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    background.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    title.setText(R.string.help_one_title);
    image.setImageResource(R.drawable.home_1);
    description.setText(R.string.help_one_description);
  }
}
