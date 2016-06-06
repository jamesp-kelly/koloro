package com.jameskelly.koloro.preferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class SimpleSpinnerAdapter extends BaseAdapter {

  private final List<String> spinnerValues;
  private final LayoutInflater layoutInflater;
  private final Context context;

  public SimpleSpinnerAdapter(Context context, List<String> values) {
    layoutInflater = LayoutInflater.from(context);
    this.context = context;
    spinnerValues = values;
  }

  @Override public int getCount() {
    return spinnerValues.size();
  }

  @Override public Object getItem(int position) {
    return spinnerValues.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView = (TextView) convertView;

    if (textView == null) {
      textView =
          (TextView) layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
      textView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
    }

    textView.setText(spinnerValues.get(position));
    return textView;
  }
}
