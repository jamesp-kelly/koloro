package com.insuleto.koloro.ui.adaptors;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.insuleto.koloro.R;
import com.insuleto.koloro.model.KoloroObj;
import java.util.List;

public class ColorRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<KoloroObj> koloroObjs;
  private ColorItemListener listener;
  private boolean showHex;
  private static final int HEADER_POSITION = 0;
  private static final int TYPE_HEADER = 0;
  private static final int TYPE_ITEM = 1;

  public ColorRecyclerAdapter(List<KoloroObj> koloroObjs, ColorItemListener listener, boolean showHex) {
    this.koloroObjs = koloroObjs;
    this.listener = listener;
    this.showHex = showHex;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (viewType == TYPE_HEADER) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.color_recycler_item_header, parent, false);
      return new ColorHeaderHolder(view);
    } else {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.color_recycler_item_vertical, parent, false);
      return new ColorViewHolder(view);
    }
  }

  @SuppressWarnings("ConstantConditions")
  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (position != HEADER_POSITION) {
      ColorViewHolder colorHolder = (ColorViewHolder) holder;
      KoloroObj koloroObj = koloroObjs.get(position - 1);
      colorHolder.colorItemLayout.setBackgroundColor(koloroObj.getColorInt());
      if (showHex) {
        colorHolder.colorItemText.setText(koloroObj.getHexString());
        colorHolder.colorItemText.setTextSize(25);
      } else {
        colorHolder.colorItemText.setText(
            String.format(KoloroObj.RGB_FORMAT, koloroObj.getRed(), koloroObj.getGreen(), koloroObj.getBlue()));
        colorHolder.colorItemText.setTextSize(22);
      }
      colorHolder.colorItemNote.setText(koloroObj.getNote());
      listener.colorTextChanged(koloroObj.getColorInt(), colorHolder.colorItemText, colorHolder.colorItemNote);

      colorHolder.copyButton.setOnClickListener(v -> {
        listener.copyButtonClicked(koloroObj);
      });

      colorHolder.noteButton.setOnClickListener(v -> listener.noteButtonClicked(koloroObj));
    } else {

      ColorHeaderHolder colorHeaderHolder = (ColorHeaderHolder) holder;
      PopupMenu popupMenu = new PopupMenu(colorHeaderHolder.savedColorsOverflow.getContext(),
          colorHeaderHolder.savedColorsOverflow);
      popupMenu.inflate(R.menu.drawer_menu);

      colorHeaderHolder.savedColorsOverflow.setOnClickListener(v -> {
        popupMenu.show();
      });

      popupMenu.setOnMenuItemClickListener(item -> {
        switch (item.getItemId()) {
          case R.id.menu_item_preferences: {
            listener.preferencesMenuItemClicked();
            break;
          }
          case R.id.menu_item_share: {
            listener.shareMenuItemClicked();
            break;
          }
          case R.id.menu_item_clear: {
            listener.clearMenuItemClicked();
            break;
          }
        }
        return true;
      });
    }
  }

  @Override public int getItemCount() {
    return koloroObjs.size() + 1;
  }

  @Override public int getItemViewType(int position) {
    if (position == 0) {
      return TYPE_HEADER;
    } else {
      return TYPE_ITEM;
    }
  }

  public static class ColorHeaderHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.saved_colors_overflow) ImageButton savedColorsOverflow;

    public ColorHeaderHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
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
    void copyButtonClicked(KoloroObj koloroObj);
    void preferencesMenuItemClicked();
    void shareMenuItemClicked();
    void clearMenuItemClicked();
  }
}
