package com.jameskelly.koloro.ui.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import io.codetail.animation.RevealAnimator;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class RevealRelativeLayout extends RelativeLayout implements RevealAnimator {

  private Path revealPath;
  private final Rect targetBounds = new Rect();
  private RevealInfo revealInfo;
  private boolean running;
  private float radius;

  public RevealRelativeLayout(Context context) {
    this(context, null);
  }

  public RevealRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RevealRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    revealPath = new Path();
  }
  
  @Override public void onRevealAnimationStart() {
    running = true;
  }

  @Override public void onRevealAnimationEnd() {
    running = false;
    invalidate(targetBounds);
  }

  @Override public void onRevealAnimationCancel() {
    onRevealAnimationEnd();
  }

  @Override public void setRevealRadius(float value) {
    radius = value;
    revealInfo.getTarget().getHitRect(targetBounds);
    invalidate(targetBounds);
  }

  @Override public float getRevealRadius() {
    return radius;
  }

  @Override public void attachRevealInfo(RevealInfo info) {
    revealInfo = info;
  }

  @Override public SupportAnimator startReverseAnimation() {
    if (revealInfo != null && revealInfo.hasTarget() && !running) {
      return ViewAnimationUtils.createCircularReveal(revealInfo.getTarget(),
          revealInfo.centerX, revealInfo.centerY,
          revealInfo.endRadius, revealInfo.startRadius);
    }
    return null;
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    if (running && child == revealInfo.getTarget()) {
      final int state = canvas.save();

      revealPath.reset();
      revealPath.addCircle(revealInfo.centerX, revealInfo.centerY, radius, Path.Direction.CW);
      canvas.clipPath(revealPath);

      boolean isInvalidated = super.drawChild(canvas, child, drawingTime);
      canvas.restoreToCount(state);

      return isInvalidated;
    }

    return super.drawChild(canvas, child, drawingTime);
  }
}
