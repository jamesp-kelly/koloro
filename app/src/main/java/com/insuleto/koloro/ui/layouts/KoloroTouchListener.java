package com.insuleto.koloro.ui.layouts;

import com.insuleto.koloro.model.TouchPoint;

public interface KoloroTouchListener {

  void onTouchEvent(TouchPoint point);
  void onLongTouchEvent();
}
