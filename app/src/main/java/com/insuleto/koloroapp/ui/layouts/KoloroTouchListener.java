package com.insuleto.koloroapp.ui.layouts;

import com.insuleto.koloroapp.model.TouchPoint;

public interface KoloroTouchListener {

  void onTouchEvent(TouchPoint point);
  void onLongTouchEvent();
}
