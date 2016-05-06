package com.jameskelly.koloro.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.ui.views.CaptureView;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.WindowManager.LayoutParams.*;

public class OverlayFrameLayout extends FrameLayout implements CaptureView {

  private final Context context;

  public OverlayFrameLayout(Context context) {
    super(context);
    this.context = context;

    inflate(context, R.layout.activity_capture, this);
  }

  @Override public WindowManager.LayoutParams setupCaptureWindowLayoutParams() {
    Resources resources = context.getResources();
    int width = resources.getDimensionPixelSize(R.dimen.overlay_width);
    int height = resources.getDimensionPixelSize(R.dimen.overlay_height);

    WindowManager.LayoutParams params =
        new WindowManager.LayoutParams(width, height, TYPE_SYSTEM_ERROR,
            FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_NO_LIMITS |
                FLAG_LAYOUT_INSET_DECOR | FLAG_LAYOUT_IN_SCREEN, TRANSLUCENT);

    params.gravity = Gravity.TOP;

    return params;
  }

  @Override public void displayCaptureOverLay() {
  }
}
