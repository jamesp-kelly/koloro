package com.jameskelly.koloro.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

public class ColorPickActivity extends BaseActivity {

  public static final String SCREEN_CAPTURE_URI = "screen_capture_uri";

  @Inject Picasso picasso;

  @BindView(R.id.screen_capture_image) ImageView screenCaptureImage;

  public static Intent intent(Context context) {
    return new Intent(context, ColorPickActivity.class);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_color_picker);
    KoloroApplication.get(this).applicationComponent().inject(this);
    ButterKnife.bind(this);

    Intent intent = getIntent();
    Uri screenCaptureUri = intent.getParcelableExtra(SCREEN_CAPTURE_URI);

    picasso.load(screenCaptureUri).into(screenCaptureImage);
  }
}
