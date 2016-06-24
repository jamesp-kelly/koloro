package com.insuleto.koloro.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZoomRect extends View {

  private int startX, startY, endX, endY;
  private Paint blackPaint;
  private Paint whitePaint;
  private ZoomRectListener listener;

  public ZoomRect(Context context, AttributeSet attrs) {
    super(context, attrs);
    setFocusable(true);
    setFocusableInTouchMode(true);
    blackPaint = new Paint();
    blackPaint.setPathEffect(new DashPathEffect(new float[] {20, 20}, 0));
    blackPaint.setColor(Color.BLACK);
    blackPaint.setStyle(Paint.Style.STROKE);
    blackPaint.setStrokeWidth(5);

    whitePaint = new Paint();
    //whitePaint.setPathEffect(new DashPathEffect(new float[] {10, 20}, 0));
    whitePaint.setColor(Color.WHITE);
    whitePaint.setStyle(Paint.Style.STROKE);
    whitePaint.setStrokeWidth(5);

  }

  public void enable() {
    setEnabled(true);
    setVisibility(View.VISIBLE);
    bringToFront();
  }

  public void disable() {
    setEnabled(false);
    setVisibility(GONE);
  }


  public void setZoomRectListener(ZoomRectListener listener) {
    this.listener = listener;
  }


  @Override protected void onDraw(Canvas canvas) {

    int drawLeft, drawTop, drawRight, drawBottom;

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

    if (drawLeft != 0) {
      canvas.drawRect(drawLeft, drawTop, drawRight, drawBottom, whitePaint);
      canvas.drawRect(drawLeft, drawTop, drawRight, drawBottom, blackPaint);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    int x = (int) event.getRawX();
    int y = (int) event.getRawY();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        startX = x;
        startY = y;
        break;
      case MotionEvent.ACTION_MOVE:
        endX = x;
        endY = y;
        break;
      case MotionEvent.ACTION_UP:
        listener.onTouchUp(startX, startY, endX, endY);
        startX = startY = endX = endY = 0;
        this.setVisibility(View.GONE);
        this.setEnabled(false);
        break;
      default:
        return false;
    }

    postInvalidate();
    return true;
  }

  public interface ZoomRectListener {
    void onTouchUp(int startX, int startY, int endX, int endY);
  }
}
