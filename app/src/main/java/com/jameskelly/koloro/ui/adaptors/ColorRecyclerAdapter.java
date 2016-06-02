package com.jameskelly.koloro.ui.adaptors;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.model.KoloroObj;
import java.util.List;

public class ColorRecyclerAdapter extends RecyclerView.Adapter<ColorRecyclerAdapter.ColorViewHolder> {

  private List<KoloroObj> koloroObjs;
  private ColorItemListener listener;
  private int orientation;

  public ColorRecyclerAdapter(List<KoloroObj> koloroObjs, ColorItemListener listener, int orientation) {
    this.koloroObjs = koloroObjs;
    this.listener = listener;
    this.orientation = orientation;
  }

  @Override public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;

    if (orientation == LinearLayoutManager.VERTICAL) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.color_recycler_item_vertical, parent, false);
    } else {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.color_recycler_item_horizontal, parent, false);
    }

    return new ColorViewHolder(view);
  }

  @Override public void onBindViewHolder(ColorViewHolder holder, int position) {
    holder.colorItemLayout.setBackgroundColor(koloroObjs.get(position).getColorInt());
    holder.colorItemText.setText(koloroObjs.get(position).getHexString());
    holder.colorItemNote.setText(koloroObjs.get(position).getNote());
    listener.colorTextChanged(koloroObjs.get(position).getColorInt(),
        holder.colorItemText, holder.colorItemNote);

    holder.copyButton.setOnClickListener(v ->
        listener.copyButtonClicked(koloroObjs.get(position).getHexString()));

    holder.noteButton.setOnClickListener(v -> listener.noteButtonClicked(koloroObjs.get(position)));
  }

  @Override public int getItemCount() {
    return koloroObjs.size();
  }

  public static class ColorViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.color_item_frame) RelativeLayout colorItemLayout;
    @BindView(R.id.color_item_text) TextView colorItemText;
    @BindView(R.id.color_item_note) TextView colorItemNote;
    @BindView(R.id.color_item_copy_button) View copyButton;
    @BindView(R.id.color_item_note_button) View noteButton;

    public ColorViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      colorItemNote.setEllipsize(TextUtils.TruncateAt.END);
    }
  }

  public interface ColorItemListener {
    void colorTextChanged(int backgroundColor, TextView... affectedViews);
    void noteButtonClicked(KoloroObj koloroObj);
    void copyButtonClicked(String hexString);
  }
}
