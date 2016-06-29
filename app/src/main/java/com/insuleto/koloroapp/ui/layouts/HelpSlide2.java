package com.insuleto.koloroapp.ui.layouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.insuleto.koloroapp.R;
import com.insuleto.koloroapp.ui.HelpFragment;

public class HelpSlide2 extends HelpFragment {

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    background.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
    title.setText(R.string.help_two_title);
    image.setImageResource(R.drawable.overlay_1);
    description.setText(R.string.help_two_description);
  }
}
