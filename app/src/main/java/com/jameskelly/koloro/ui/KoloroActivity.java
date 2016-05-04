package com.jameskelly.koloro.ui;

import android.os.Bundle;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;

public class KoloroActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    KoloroApplication.get(this).applicationComponent().inject(this);


    setContentView(R.layout.activity_koloro);
  }
}
