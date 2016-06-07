package com.jameskelly.koloro.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.ui.adaptors.HelpPagerAdapter;

public class HelpActivity extends BaseActivity {

  @BindView(R.id.help_view_pager) ViewPager helpViewPager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);
    ButterKnife.bind(this);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    helpViewPager.setAdapter(new HelpPagerAdapter(this));
  }
}
