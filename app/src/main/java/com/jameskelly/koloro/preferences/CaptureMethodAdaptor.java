package com.jameskelly.koloro.preferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;

public class CaptureMethodAdaptor extends BaseAdapter {

  private final List<String> captureMethods = Arrays.asList("Onscreen button", "Volume down");
  private final LayoutInflater layoutInflater;

  public CaptureMethodAdaptor(Context context) {
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public int getCount() {
    return captureMethods.size();
  }

  @Override public Object getItem(int position) {
    return captureMethods.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView = (TextView) convertView;

    if (textView == null) {
      textView =
          (TextView) layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
    }

    textView.setText(captureMethods.get(position));
    return textView;
  }
}
