package com.jameskelly.koloro.ui.layouts;

import android.animation.Animator;

public abstract class AnimatorEndListener implements Animator.AnimatorListener {
  @Override public void onAnimationStart(Animator animation) {
  }

  @Override public abstract void onAnimationEnd(Animator animation);

  @Override public void onAnimationCancel(Animator animation) {
  }

  @Override public void onAnimationRepeat(Animator animation) {
  }
}
