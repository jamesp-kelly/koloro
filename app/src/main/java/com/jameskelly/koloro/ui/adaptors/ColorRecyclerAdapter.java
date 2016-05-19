package com.jameskelly.koloro.ui.adaptors;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.model.KoloroObj;
import java.util.List;

public class ColorRecyclerAdapter extends RecyclerView.Adapter<ColorRecyclerAdapter.ColorViewHolder> {

  private List<KoloroObj> koloroObjs;

  public ColorRecyclerAdapter(List<KoloroObj> koloroObjs) {
    this.koloroObjs = koloroObjs;
  }

  @Override public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.color_recycler_item, parent, false);

    return new ColorViewHolder(view);
  }

  @Override public void onBindViewHolder(ColorViewHolder holder, int position) {
    Log.i("TESTING", String.valueOf(koloroObjs.get(position).getColorInt()));

    holder.itemLayout.setBackgroundColor(koloroObjs.get(position).getColorInt());
  }

  @Override public int getItemCount() {
    return koloroObjs.size();
  }

  public static class ColorViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.color_item_frame) FrameLayout itemLayout;

    public ColorViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
