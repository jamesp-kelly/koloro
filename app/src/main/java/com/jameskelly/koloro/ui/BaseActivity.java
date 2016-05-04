package com.jameskelly.koloro.ui;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import com.jameskelly.koloro.R;

public class BaseActivity extends AppCompatActivity {

  private Toolbar toolbar;

  @Override public void setContentView(View view) {
    super.setContentView(view);
    setupToolbar();
  }

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    setupToolbar();
  }

  @Override public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(view, params);
    setupToolbar();
  }

  private void setupToolbar() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }
}
