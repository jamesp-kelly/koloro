package com.jameskelly.koloro.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.events.ImageProcessedEvent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ColorPickActivity extends BaseActivity {

  public static final String SCREEN_CAPTURE_URI = "screen_capture_uri";

  private Bitmap capturedBitmap;
  private float parentX, parentY;

  @Inject Picasso picasso;
  @Inject ClipboardManager clipboardManager;

  @BindView(R.id.color_details_parent) LinearLayout colorDetailsParent;
  @BindView(R.id.color_details_layout) FrameLayout colorDetailsLayout;
  @BindView(R.id.copy_button) ImageButton copyButton;
  @BindView(R.id.screen_capture_image) ImageView screenCaptureImage;
  @BindView(R.id.hex_text) TextView hexText;

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

  @Override protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override protected void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @OnTouch(R.id.screen_capture_image)
  boolean captureImageTouch(View v, MotionEvent event) {

    int touchX = Math.round(event.getRawX());
    int touchY = Math.round(event.getRawY());

    String touchedHexColor = getColorAtPoint(touchX, touchY);
    int touchedColor = Color.parseColor(touchedHexColor);

    GradientDrawable background = (GradientDrawable) colorDetailsLayout.getBackground();
    background.setColor(touchedColor);

    hexText.setText(touchedHexColor);
    hexText.setTextColor(getHexTextColor(touchedColor));

    colorDetailsParent.setVisibility(View.VISIBLE);

    return false;
  }

  @OnTouch(R.id.color_details_layout)
  boolean colorLayoutTouch(View v, MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        parentX = colorDetailsParent.getX() - event.getRawX();
        parentY = colorDetailsParent.getY() - event.getRawY();
        break;
      case MotionEvent.ACTION_MOVE:
        colorDetailsParent.animate()
            .x(event.getRawX() + parentX)
            .y(event.getRawY() + parentY)
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
    Toast.makeText(this, R.string.copied_clipboard_toast, Toast.LENGTH_SHORT).show();
  }

  @Subscribe
  public void onEventRecieved(ImageProcessedEvent event) {

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

  private int getHexTextColor(int backgroundColor) {
    int resultColorValue = 0;

    int r = Color.red(backgroundColor);
    int g = Color.green(backgroundColor);
    int b = Color.blue(backgroundColor);

    double backgroundBrightness = 1 - (0.299 * Color.red(backgroundColor) + 0.587 * Color.green(backgroundColor)
        + 0.114 * Color.blue(backgroundColor))/255;

    if (backgroundBrightness < 0.5) {
      //bright color, use black
      resultColorValue = 0;
    } else {
      resultColorValue = 255;
    }

    return Color.argb(255, resultColorValue, resultColorValue, resultColorValue);
  }
}
