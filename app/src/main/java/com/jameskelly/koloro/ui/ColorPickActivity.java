package com.jameskelly.koloro.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import javax.inject.Inject;

public class ColorPickActivity extends BaseActivity {

  public static final String SCREEN_CAPTURE_URI = "screen_capture_uri";

  private Bitmap capturedBitmap;
  private float dX, dY;

  @Inject Picasso picasso;
  @Inject ClipboardManager clipboardManager;

  @BindView(R.id.color_details_layout) FrameLayout colorDetailsLayout;
  @BindView(R.id.screen_capture_image) ImageView screenCaptureImage;
  @BindView(R.id.hex_text) EditText hexText;
  @BindView(R.id.copy_button) Button copyButton;

  public static Intent intent(Context context) {
    return new Intent(context, ColorPickActivity.class);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_color_picker);
    KoloroApplication.get(this).applicationComponent().inject(this);
    ButterKnife.bind(this);

    Intent intent = getIntent();
    Uri screenCaptureUri = intent.getParcelableExtra(SCREEN_CAPTURE_URI);

    picasso.load(screenCaptureUri).into(colorPickerTarget);
  }

  @OnTouch(R.id.screen_capture_image)
  boolean captureImageTouch(View v, MotionEvent event) {

    int touchX = Math.round(event.getRawX());
    int touchY = Math.round(event.getRawY());

    String touchedHexColor = getColorAtPoint(touchX, touchY);
    int touchedColor = Color.parseColor(touchedHexColor);

    colorDetailsLayout.setBackgroundColor(touchedColor);
    hexText.setText(touchedHexColor);
    colorDetailsLayout.setVisibility(View.VISIBLE);

    return false;
  }

  @OnTouch(R.id.color_details_layout)
  boolean colorLayoutTouch(View v, MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        dX = v.getX() - event.getRawX();
        dY = v.getY() - event.getRawY();
        break;
      case MotionEvent.ACTION_MOVE:
        v.animate()
            .x(event.getRawX() + dX)
            .y(event.getRawY() + dY)
            .setDuration(0)
            .start();
        break;
      default:
        return false;
    }
    return true;
  }

  @OnClick(R.id.copy_button)
  void onClick() {
    ClipData clip = ClipData.newPlainText("Copied text", hexText.getText().toString());
    clipboardManager.setPrimaryClip(clip);
  }

  Target colorPickerTarget = new Target() {
    @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
      capturedBitmap = bitmap;
      screenCaptureImage.setImageBitmap(bitmap);
    }

    @Override public void onBitmapFailed(Drawable errorDrawable) {
    }

    @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
  };

  private String getColorAtPoint(int x, int y) {
    int pixelColor = capturedBitmap.getPixel(x, y);

    int r = Color.red(pixelColor);
    int g = Color.green(pixelColor);
    int b = Color.blue(pixelColor);

    return String.format("#%02x%02x%02x", r, g, b);
  }


}
