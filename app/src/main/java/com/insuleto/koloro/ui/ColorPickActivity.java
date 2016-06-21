package com.insuleto.koloro.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.insuleto.koloro.KoloroApplication;
import com.insuleto.koloro.R;
import com.insuleto.koloro.model.ColorFormat;
import com.insuleto.koloro.model.KoloroObj;
import com.insuleto.koloro.model.RgbColor;
import com.insuleto.koloro.preferences.BooleanPreference;
import com.insuleto.koloro.preferences.PreferencesModule;
import com.insuleto.koloro.ui.adaptors.ColorRecyclerAdapter;
import com.insuleto.koloro.ui.adaptors.ColorRecyclerAdapter.ColorItemListener;
import com.insuleto.koloro.ui.presenters.ColorPickerPresenter;
import com.insuleto.koloro.ui.views.ColorPickerView;
import com.insuleto.koloro.ui.views.ZoomRect;
import com.insuleto.koloro.util.FirebaseEvents;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import javax.inject.Inject;
import javax.inject.Named;

public class ColorPickActivity extends BaseActivity implements ColorPickerView {

  public static final String SCREEN_CAPTURE_URI = "screen_capture_uri";

  private Bitmap capturedBitmap;
  private float parentX, parentY;
  private boolean imageIsZoomed = false;

  private int currentlySelectedColor;
  private String currentlySelectedColorHex;
  private RgbColor currentlySelectedColorRgb;
  private ColorRecyclerAdapter colorRecyclerAdapter;
  private Uri imageUri;

  @Inject Picasso picasso;
  @Inject ClipboardManager clipboardManager;
  @Inject ColorPickerPresenter presenter;
  @Inject FirebaseAnalytics firebaseAnalytics;

  @Inject @Named(PreferencesModule.STORE_CAPTURES_IN_GALLERY_KEY) BooleanPreference galleryPreference;
  @Inject @Named(PreferencesModule.COLOR_FORMAT_KEY) int colorFormatPreference;

  @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
  @BindView(R.id.color_details_parent) LinearLayout colorDetailsParent;
  @BindView(R.id.color_details_layout) FrameLayout colorDetailsLayout;
  @BindView(R.id.save_button) Button saveButton;
  @BindView(R.id.copy_button) Button copyButton;
  @BindView(R.id.screen_capture_image) ImageView screenCaptureImage;
  @BindView(R.id.hex_text) TextView hexText;
  @BindView(R.id.rgb_text) TextView rgbText;
  @BindView(R.id.color_list_recycler) RecyclerView colorRecycler;
  @BindView(R.id.zoom_rect) ZoomRect zoomRect;

  public static Intent intent(Context context) {
    return new Intent(context, ColorPickActivity.class);
  }

  @SuppressWarnings("ResourceType")
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_color_picker);
    setRequestedOrientation(getAppropiateOrientation());
    KoloroApplication.get(this).applicationComponent().inject(this);
    ButterKnife.bind(this);

    zoomRect.setEnabled(false);

    presenter.bindView(this);
    presenter.setupRealm();

    colorRecycler.setLayoutManager(new LinearLayoutManager(this));
    colorRecyclerAdapter = new ColorRecyclerAdapter(presenter.getAllKoloroObjects(),
        colorItemListener, LinearLayoutManager.VERTICAL, colorFormatPreference == ColorFormat.HEX);
    colorRecycler.setAdapter(colorRecyclerAdapter);

    if (colorFormatPreference == ColorFormat.HEX) {
      rgbText.setVisibility(View.GONE);
      hexText.setVisibility(View.VISIBLE);
    } else {
      rgbText.setVisibility(View.VISIBLE);
      hexText.setVisibility(View.GONE);
    }
  }

  ColorItemListener colorItemListener = new ColorItemListener() {
    @Override public void colorTextChanged(int backgroundColor, TextView... affectedViews) {
      int contrastingTextColor = presenter.getContrastingTextColor(backgroundColor);
      for (TextView view : affectedViews) {
        view.setTextColor(contrastingTextColor);
      }
    }

    @Override public void noteButtonClicked(KoloroObj koloroObj) {
      new MaterialDialog.Builder(ColorPickActivity.this)
          .title(R.string.saved_color_note_title)
          .inputType(InputType.TYPE_CLASS_TEXT)
          .input(getString(R.string.note_hint), koloroObj.getNote(), (dialog, input) -> {
            //don't need to do anything here
          })
          .positiveText(R.string.save_button)
          .negativeText(R.string.cancel_button)
          .onPositive((dialog, which) -> {
            if (dialog.getInputEditText() != null) {
              String inputValue = dialog.getInputEditText().getText().toString();
              presenter.saveNote(koloroObj, inputValue);
              Bundle bundle = new Bundle();
              firebaseAnalytics.logEvent(FirebaseEvents.NOTE_ADDED, null);
            }
          })
          .show();
    }

    @Override public void copyButtonClicked(KoloroObj koloroObj) {
      String colorString;
      if (colorFormatPreference == ColorFormat.HEX) {
        colorString = koloroObj.getHexString();
      } else {
        colorString = koloroObj.getRgbString();
      }
      copyColorString(colorString);
    }
  };

  private void copyColorString(String colorString) {
    ClipData clip = ClipData.newPlainText("Copied text", colorString);
    clipboardManager.setPrimaryClip(clip);
    Toast.makeText(this, R.string.copied_clipboard_toast, Toast.LENGTH_SHORT).show();
    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, colorString);
    firebaseAnalytics.logEvent(FirebaseEvents.COLOR_COPIED, bundle);
  }

  @OnClick(R.id.zoom_button)
  void onZoomClicked() {
    if (capturedBitmap != null) {

      zoomRect.setEnabled(true);
      zoomRect.setVisibility(View.VISIBLE);
      zoomRect.bringToFront();

      zoomRect.setZoomRectListener((startX, startY, endX, endY) -> {
        int drawLeft, drawTop, drawRight, drawBottom; //copy, paste, YEAH

        if (startX < endX) {
          drawLeft = startX;
          drawRight = endX;
        } else {
          drawLeft = endX;
          drawRight = startX;
        }

        if (startY < endY) {
          drawTop = startY;
          drawBottom = endY;
        } else {
          drawTop = endY;
          drawBottom = startY;
        }

        capturedBitmap = Bitmap.createBitmap(capturedBitmap, drawLeft, drawTop, drawRight - drawLeft, drawBottom - drawTop);
        screenCaptureImage.setImageBitmap(capturedBitmap);
        imageIsZoomed = true;
      });
    }
  }

  @Override public void onBackPressed() {
    if (imageIsZoomed) {
      displayCaptureImage(this.imageUri); //revert image
    } else {
      super.onBackPressed();
    }
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

    if (!galleryPreference.get()) {
      presenter.removeStoredScreenShot();
    }

    super.onStop();
  }

  @Override protected void onDestroy() {
    presenter.unbindView(this);
    super.onDestroy();
  }

  @Override public void displayCaptureImage(Uri imageUri) {
    this.imageUri = imageUri;
    picasso.load(imageUri).into(colorPickerTarget);
  }

  @OnTouch(R.id.screen_capture_image)
  boolean captureImageTouch(View v, MotionEvent event) {

    int touchX = Math.round(event.getX());
    int touchY = Math.round(event.getY());

    if (imageIsZoomed) {
      getScaledTouchCoords(touchX, touchY);

    } else { //we dont have to worry about scaling the touch coords
      updateColorDetails(touchX, touchY);
    }


    return true;
  }

  private void getScaledTouchCoords(int touchX, int touchY) {
    float xRatio = (float)capturedBitmap.getWidth() / screenCaptureImage.getWidth();
    float yRatio = (float)capturedBitmap.getHeight() / screenCaptureImage.getHeight();


    //need to adjust y if there is vertical whitespace, x if horizontal whitespace
    int scaledX, scaledY;

    if (xRatio > yRatio) {

      //white space will be vertical

      //int drawnHeight = Math.round(capturedBitmap.getHeight() * xRatio);
      //int whitespace = screenCaptureImage.getHeight() - drawnHeight;
      //
      //
      //scaledX = Math.round(touchX * xRatio);
      //scaledY = Math.round((touchY * yRatio) + (whitespace / 2));

    } else {
      //whitespace will be horizontal

      //int drawnWidth = Math.round(capturedBitmap.getWidth() * yRatio);
      //int whitespace = screenCaptureImage.getWidth() - drawnWidth;
      //
      //scaledX = Math.round((touchX * xRatio) + (whitespace / 2));
      //scaledY = Math.round(touchY * yRatio);
    }

    scaledX = Math.round((touchX * xRatio));
    scaledY = Math.round(touchY * yRatio);
    updateColorDetails(scaledX, scaledY);
  }

  private void updateColorDetails(int touchX, int touchY) {
    if (capturedBitmap != null) {
      currentlySelectedColor = capturedBitmap.getPixel(touchX, touchY);
      currentlySelectedColorHex = presenter.generateHexColor(currentlySelectedColor);
      currentlySelectedColorRgb = presenter.generateRgbColor(currentlySelectedColor);


      GradientDrawable background = (GradientDrawable) colorDetailsLayout.getBackground();
      background.setColor(currentlySelectedColor);

      hexText.setText(currentlySelectedColorHex);
      hexText.setTextColor(presenter.getContrastingTextColor(currentlySelectedColor));

      rgbText.setText(String.format(KoloroObj.RGB_FORMAT, currentlySelectedColorRgb.getR(),
          currentlySelectedColorRgb.getG(), currentlySelectedColorRgb.getB()));
      rgbText.setTextColor(presenter.getContrastingTextColor(currentlySelectedColor));

      colorDetailsParent.setVisibility(View.VISIBLE);
    }
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
    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, currentlySelectedColorHex);
    firebaseAnalytics.logEvent(FirebaseEvents.COLOR_SAVED, bundle);
  }

  @OnClick(R.id.copy_button)
  void onCopyClicked() {
    String colorString;
    if (colorFormatPreference == ColorFormat.HEX) {
      colorString = hexText.getText().toString();
    } else {
      colorString = rgbText.getText().toString();
    }

    copyColorString(colorString);
  }


  private int getAppropiateOrientation() {
    int orientation = getResources().getConfiguration().orientation;
    int rotation = getWindowManager().getDefaultDisplay().getRotation();

    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    } else {
      if (rotation == Surface.ROTATION_270) {
        return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
      } else {
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
      }
    }
  }

  @Override public void updateColorList() {
    colorRecyclerAdapter.notifyDataSetChanged();
  }

  Target colorPickerTarget = new Target() {
    @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
      capturedBitmap = bitmap;
      screenCaptureImage.setImageBitmap(bitmap);
      updateColorDetails(200, 200); //simulate touch to display the color picker
    }

    @Override public void onBitmapFailed(Drawable errorDrawable) {
    }

    @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
  };

}
