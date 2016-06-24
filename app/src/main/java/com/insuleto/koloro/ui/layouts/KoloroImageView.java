package com.insuleto.koloro.ui.layouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.insuleto.koloro.R;
import com.insuleto.koloro.model.TouchPoint;

public class KoloroImageView extends ImageView implements View.OnTouchListener {

  private static final int DISTANCE_THRESHOLD = 40;
  private static final int LONG_CLICK_CHECK_DURATION = 500;

  private KoloroTouchListener koloroTouchListener;
  private Context context;
  private boolean twoFingersDown = false;
  private Handler handler;

  public KoloroImageView(Context context) {
    this(context, null);
  }

  public KoloroImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    this.setOnTouchListener(this);
    handler = new Handler(Looper.getMainLooper());
  }

  public void setKoloroListener(KoloroTouchListener listener) {
    this.setEnabled(true);
    this.koloroTouchListener = listener;
  }

  public void enable() {
    enable(null);
  }

  public void enable(Bitmap bitmap) {
    this.setEnabled(true);
    this.setColorFilter(null);
    if (bitmap != null) {
      this.setVisibility(VISIBLE);
      this.setImageBitmap(bitmap);
    }
  }

  public void clear() { //used just for zoomed image
    this.setImageDrawable(null);
    this.setVisibility(GONE);
  }

  public void disable() {
    this.setEnabled(false);
    this.setColorFilter(getResources().getColor(R.color.zoomed_image_background));
  }

  @Override public boolean onTouch(View v, MotionEvent event) {

    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN: {
        if (!twoFingersDown) {
          koloroTouchListener.onTouchEvent(
              new TouchPoint(Math.round(event.getX()), Math.round(event.getY())));
        }
        break;
      }
      case MotionEvent.ACTION_POINTER_DOWN: {
        twoFingersDown = true;
        handler.postDelayed(() -> {
          if (twoFingersDown) {
            koloroTouchListener.onLongTouchEvent();
          }
        }, LONG_CLICK_CHECK_DURATION);
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        if (!twoFingersDown) {
          koloroTouchListener.onTouchEvent(new TouchPoint(Math.round(event.getX()), Math.round(event.getY())));
        }
        break;
      }
      case MotionEvent.ACTION_POINTER_UP: {
        if (event.getPointerCount() == 2) {
          twoFingersDown = false;
        }
        break;
      }
    }

    return true;
  }
}
