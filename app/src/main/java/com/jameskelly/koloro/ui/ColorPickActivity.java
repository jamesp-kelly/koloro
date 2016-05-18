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
import com.jameskelly.koloro.ui.presenters.ColorPickerPresenter;
import com.jameskelly.koloro.ui.views.ColorPickerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import javax.inject.Inject;

public class ColorPickActivity extends BaseActivity implements ColorPickerView {

  public static final String SCREEN_CAPTURE_URI = "screen_capture_uri";

  private Bitmap capturedBitmap;
  private float parentX, parentY;

  private int currentlySelectedColor;
  private String currentlySelectedColorHex;

  @Inject Picasso picasso;
  @Inject ClipboardManager clipboardManager;
  @Inject ColorPickerPresenter presenter;

  @BindView(R.id.color_details_parent) LinearLayout colorDetailsParent;
  @BindView(R.id.color_details_layout) FrameLayout colorDetailsLayout;
  @BindView(R.id.save_button) ImageButton saveButton;
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

    presenter.bindView(this);
  }

  @Override protected void onResume() {
    super.onResume();
    screenCaptureImage.setImageDrawable(null);
  }

  @Override protected void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override protected void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override public void displayCaptureImage(Uri imageUri) {
    picasso.load(imageUri).into(colorPickerTarget);
  }

  @OnTouch(R.id.screen_capture_image)
  boolean captureImageTouch(View v, MotionEvent event) {

    int touchX = Math.round(event.getRawX());
    int touchY = Math.round(event.getRawY());

    currentlySelectedColorHex = presenter.generateHexColor(capturedBitmap.getPixel(touchX, touchY));
    currentlySelectedColor = Color.parseColor(currentlySelectedColorHex);

    GradientDrawable background = (GradientDrawable) colorDetailsLayout.getBackground();
    background.setColor(currentlySelectedColor);

    hexText.setText(currentlySelectedColorHex);
    hexText.setTextColor(presenter.getContrastingTextColor(currentlySelectedColor));

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

  @OnClick(R.id.save_button)
  void onSaveClicked() {
    Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
    presenter.saveColor(currentlySelectedColor, currentlySelectedColorHex);
  }

  @OnClick(R.id.copy_button)
  void onCopyClicked() {
    ClipData clip = ClipData.newPlainText("Copied text", hexText.getText().toString());
    clipboardManager.setPrimaryClip(clip);
    Toast.makeText(this, R.string.copied_clipboard_toast, Toast.LENGTH_SHORT).show();
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
}
