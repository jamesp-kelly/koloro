package com.jameskelly.koloro.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.ui.views.CaptureView;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.WindowManager.LayoutParams.*;

public class OverlayButtonsLayout extends FrameLayout implements CaptureView {

  private final Context context;
  private OverlayClickListener overlayClickListener;

  public OverlayButtonsLayout(Context context, OverlayClickListener overlayClickListener) {
    super(context);
    this.context = context;
    this.overlayClickListener = overlayClickListener;
    inflate(context, R.layout.capture_buttons_overlay, this);

    ButterKnife.bind(this);
  }

  @OnClick(R.id.captureButton)
  void captureClicked() {
    overlayClickListener.onCaptureClicked();
  }

  @OnClick(R.id.cancelButton)
  void cancelClicked() {
    overlayClickListener.onCancelClicked();
  }

  @Override public WindowManager.LayoutParams setupCaptureWindowLayoutParams() {
    Resources resources = context.getResources();
    int width = resources.getDimensionPixelSize(R.dimen.overlay_buttons_width);
    int height = resources.getDimensionPixelSize(R.dimen.overlay_buttons_height);

    WindowManager.LayoutParams params =
        new WindowManager.LayoutParams(width, height, TYPE_SYSTEM_ERROR,
            FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_NO_LIMITS |
                FLAG_LAYOUT_INSET_DECOR | FLAG_LAYOUT_IN_SCREEN, TRANSLUCENT);

    params.gravity = Gravity.TOP | Gravity.RIGHT;

    return params;
  }

  @Override public void displayCaptureOverLay() {
  }

  public interface OverlayClickListener {
    void onCaptureClicked();
    void onCancelClicked();
  }
}
