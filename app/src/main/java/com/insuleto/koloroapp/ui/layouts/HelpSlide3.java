package com.insuleto.koloroapp.ui.layouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.insuleto.koloroapp.R;
import com.insuleto.koloroapp.ui.HelpFragment;

public class HelpSlide3 extends HelpFragment {

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    background.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    title.setText(R.string.help_three_title);
    image.setImageResource(R.drawable.ic_colorize_white_24dp);
    description.setText(R.string.help_three_description);
  }
}
