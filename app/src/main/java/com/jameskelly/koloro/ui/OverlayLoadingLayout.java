package com.jameskelly.koloro.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.jameskelly.koloro.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

public class OverlayLoadingLayout extends FrameLayout {

  public OverlayLoadingLayout(Context context) {
    super(context);
    inflate(context, R.layout.loading_overlay, this);
  }

  public WindowManager.LayoutParams setupLoadingOverlayParams() {
    WindowManager.LayoutParams params =
        new WindowManager.LayoutParams(MATCH_PARENT, MATCH_PARENT, TYPE_SYSTEM_ALERT,
            FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN, PixelFormat.OPAQUE);

    params.alpha = 0.5f;
    params.gravity = Gravity.TOP;

    return params;
  }

  public void testing() {
    this.getVisibility();
  }
}
