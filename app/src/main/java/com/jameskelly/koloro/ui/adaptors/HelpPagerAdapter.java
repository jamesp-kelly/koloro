package com.jameskelly.koloro.ui.adaptors;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.model.HelpPagerEnum;

public class HelpPagerAdapter extends PagerAdapter {

  private Context context;

  //@BindView(R.id.help_image) ImageView helpImage;
  //@BindView(R.id.help_text) TextView helpText;

  public HelpPagerAdapter(Context context) {
    this.context = context;
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    HelpPagerEnum helpPagerEnum = HelpPagerEnum.values()[position];
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.help_pager_page, container, false);
    container.addView(view);
    //ButterKnife.bind(this, view);

    ImageView helpImage = (ImageView) view.findViewById(R.id.help_image);
    TextView helpText = (TextView) view.findViewById(R.id.help_text);

    helpImage.setImageResource(HelpPagerEnum.values()[position].getImageRes());
    helpText.setText(HelpPagerEnum.values()[position].getStringRes());

    return view;
  }

  @Override public void setPrimaryItem(ViewGroup container, int position, Object object) {
    //View view = (View) object;
    //ImageView helpImage = (ImageView) view.findViewById(R.id.help_image);
    //TextView helpText = (TextView) view.findViewById(R.id.help_text);
    //
    //helpImage.setImageResource(HelpPagerEnum.values()[position].getImageRes());
    //helpText.setText(HelpPagerEnum.values()[position].getStringRes());
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View)object);
  }

  @Override public int getCount() {
    return HelpPagerEnum.values().length;
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }
}
