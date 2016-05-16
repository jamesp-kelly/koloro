package com.jameskelly.koloro.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import com.jameskelly.koloro.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

public class ScreenCaptureFlashLayout extends FrameLayout {

  private final GradientDrawable border;
  private final int borderColor;
  private final FlashListener flashListener;

  public ScreenCaptureFlashLayout(Context context, FlashListener flashListener) {
    super(context);
    inflate(context, R.layout.loading_overlay, this);

    this.flashListener = flashListener;
    borderColor = context.getResources().getColor(R.color.colorPrimary);

    border = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
        new int[] { Color.TRANSPARENT, Color.TRANSPARENT});
    border.setStroke(5, borderColor);
    border.mutate();

    this.setBackground(border);

  }

  public WindowManager.LayoutParams layoutParams() {
    WindowManager.LayoutParams params =
        new WindowManager.LayoutParams(MATCH_PARENT, MATCH_PARENT, TYPE_SYSTEM_ALERT,
            FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_INSET_DECOR, PixelFormat.OPAQUE);

    params.alpha = 0.5f;
    params.gravity = Gravity.TOP;

    return params;
  }

  @Override protected void onAttachedToWindow() {

    ValueAnimator animator = ValueAnimator.ofInt(0, 30, 0);
    animator.setInterpolator(new LinearInterpolator());
    animator.setDuration(200);

    animator.addUpdateListener(animation ->
        border.setStroke((Integer) animation.getAnimatedValue(), borderColor));
    animator.addListener(animListener);
    animator.start();

    super.onAttachedToWindow();
  }

  private Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
    @Override public void onAnimationStart(Animator animation) {
    }

    @Override public void onAnimationEnd(Animator animation) {
      flashListener.onFlashComplete();
    }

    @Override public void onAnimationCancel(Animator animation) {
    }

    @Override public void onAnimationRepeat(Animator animation) {
    }
  };

  public interface FlashListener {
    void onFlashComplete();
  }

}
