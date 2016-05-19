package com.jameskelly.koloro.ui.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.model.KoloroObj;
import java.util.List;

public class ColorRecyclerAdapter extends RecyclerView.Adapter<ColorRecyclerAdapter.ColorViewHolder> {

  private List<KoloroObj> koloroObjs;
  private ColorTextChangeListener listener;

  public ColorRecyclerAdapter(List<KoloroObj> koloroObjs, ColorTextChangeListener listener) {
    this.koloroObjs = koloroObjs;
    this.listener = listener;
  }

  @Override public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.color_recycler_item, parent, false);

    return new ColorViewHolder(view);
  }

  @Override public void onBindViewHolder(ColorViewHolder holder, int position) {
    holder.colorItemLayout.setBackgroundColor(koloroObjs.get(position).getColorInt());
    holder.colorItemText.setText(koloroObjs.get(position).getHexString());
    listener.colorTextChanged(holder.colorItemText, koloroObjs.get(position).getColorInt());
  }

  @Override public int getItemCount() {
    return koloroObjs.size();
  }

  public static class ColorViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.color_item_frame) FrameLayout colorItemLayout;
    @BindView(R.id.color_item_text) TextView colorItemText;

    public ColorViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface ColorTextChangeListener {
    void colorTextChanged(TextView view, int backgroundColor);
  }
}
