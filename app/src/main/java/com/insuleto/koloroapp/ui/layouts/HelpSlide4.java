package com.insuleto.koloroapp.ui.layouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.insuleto.koloroapp.R;
import com.insuleto.koloroapp.ui.HelpFragment;

public class HelpSlide4 extends HelpFragment {

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    background.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
    title.setText(R.string.help_four_title);
    image.setImageResource(R.drawable.ic_content_copy_black_24dp);
    description.setText(R.string.help_four_description);
  }
}
