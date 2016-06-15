package com.jameskelly.koloro.ui.layouts;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.ui.views.CaptureView;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

public class OverlayButtonsLayout extends FrameLayout implements CaptureView {

  private static final int ANIM_IN_DURATION = 300;
  private static final int ANIM_OUT_DURATION = 150;

  private OverlayClickListener overlayClickListener;

  @BindDimen(R.dimen.overlay_buttons_width) int buttonsWidth;
  @BindDimen(R.dimen.overlay_buttons_height) int buttonsHeight;

  @BindView(R.id.captureButton) ImageButton captureButtton;
  @BindView(R.id.cancelButton) ImageButton cancelButton;

  public OverlayButtonsLayout(Context context, OverlayClickListener overlayClickListener) {
    super(context);
    this.overlayClickListener = overlayClickListener;
    inflate(context, R.layout.capture_buttons_overlay, this);

    ButterKnife.bind(this);
  }

  @OnClick(R.id.captureButton)
  void captureClicked() {
    captureButtton.setVisibility(INVISIBLE);
    cancelButton.setVisibility(INVISIBLE);

    overlayClickListener.onCaptureClicked();
  }

  @OnClick(R.id.cancelButton)
  void cancelClicked() {
    hideCaptureOverlay();
  }

  @OnLongClick(R.id.cancelButton)
  boolean cancelLongClicked() {
    overlayClickListener.onCancelLongClicked();
    return false;
  }


  @Override public WindowManager.LayoutParams layoutParams(int positionPreference) {

    WindowManager.LayoutParams params =
        new WindowManager.LayoutParams(buttonsWidth, buttonsHeight, TYPE_SYSTEM_ERROR,
            FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_NO_LIMITS |
                FLAG_LAYOUT_INSET_DECOR | FLAG_LAYOUT_IN_SCREEN, TRANSLUCENT);

    if (positionPreference == 0) {
      params.gravity = Gravity.TOP | Gravity.RIGHT;
    } else if (positionPreference == 1) {
      params.gravity = Gravity.TOP | Gravity.LEFT;
    }

    return params;
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    displayCaptureOverlay();
  }

  @Override public void displayCaptureOverlay() {
    setTranslationY(buttonsHeight);
    animate().translationY(0).setDuration(ANIM_IN_DURATION).
        setInterpolator(new DecelerateInterpolator());
  }

  @Override public void hideCaptureOverlay() {
    animate().translationY(buttonsHeight).setDuration(ANIM_OUT_DURATION).
        setInterpolator(new DecelerateInterpolator()).withEndAction(
        new Runnable() {
          @Override public void run() {
            overlayClickListener.onCancelClicked();
          }
        });
  }

  public interface OverlayClickListener {
    void onCaptureClicked();
    void onCancelClicked();
    void onCancelLongClicked();
  }
}
