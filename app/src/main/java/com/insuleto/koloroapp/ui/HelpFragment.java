package com.insuleto.koloroapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.insuleto.koloroapp.R;

public class HelpFragment extends Fragment {


  @BindView(R.id.help_background) protected View background;
  @BindView(R.id.help_title) protected TextView title;
  @BindView(R.id.help_image) protected ImageView image;
  @BindView(R.id.help_description) protected TextView description;


  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_help_page, container, false);
    ButterKnife.bind(this, v);
    return v;
  }
}
