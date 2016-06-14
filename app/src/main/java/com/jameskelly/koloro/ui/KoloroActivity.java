package com.jameskelly.koloro.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jameskelly.koloro.KoloroApplication;
import com.jameskelly.koloro.R;
import com.jameskelly.koloro.model.KoloroObj;
import com.jameskelly.koloro.service.KoloroService;
import com.jameskelly.koloro.ui.adaptors.ColorRecyclerAdapter;
import com.jameskelly.koloro.ui.presenters.KoloroPresenter;
import com.jameskelly.koloro.ui.views.KoloroView;
import com.jameskelly.koloro.util.FirebaseEvents;
import javax.inject.Inject;

public class KoloroActivity extends BaseActivity implements KoloroView {

  private ColorRecyclerAdapter colorRecyclerAdapter;

  @Inject MediaProjectionManager mediaProjectionManager;
  @Inject KoloroPresenter presenter;
  @Inject FirebaseAnalytics firebaseAnalytics;
  @Inject ClipboardManager clipboardManager;

  @BindDimen(R.dimen.prefs_layout_margin_top) int prefsLayoutMarginTop;
  @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
  @BindView(R.id.prefs_layout) ViewGroup prefsLayout;
  @BindView(R.id.start_capture) Button startCaptureButton;
  @BindView(R.id.adView) AdView adView;
  @BindView(R.id.color_list_recycler) RecyclerView colorRecycler;

  @Override protected void onCreate(Bundle savedInstanceState) {

    KoloroApplication.get(this).applicationComponent().inject(this);

    if (1 != 1 && mediaProjectionManager != null) { //premium quicklaunch and check permission
      //Intent quickLaunchIntent = new Intent(this, QuickLaunchActivity.class);
      //startActivity(quickLaunchIntent);
      //finish();
      setTheme(R.style.AppTheme_Translucent);

      Intent mediaCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
      startActivityForResult(mediaCaptureIntent, CREATE_SCREEN_CAPTURE);
      super.onCreate(savedInstanceState);
    } else {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_koloro);
      ButterKnife.bind(this);
      presenter.bindView(this);

      setupSavedColorList();

      getSupportFragmentManager().beginTransaction()
          .replace(R.id.prefs_layout, new PreferenceFragment())
          .commit();

      MobileAds.initialize(getApplicationContext(), getString(R.string.ad_app_id));
      AdRequest adRequest = new AdRequest.Builder().build();
      adView.loadAd(adRequest);
    }
  }

  void setupSavedColorList() {
    if (!presenter.realmActive()) {
      presenter.setupRealm();
    }

    colorRecycler.setLayoutManager(new LinearLayoutManager(this));
    colorRecyclerAdapter = new ColorRecyclerAdapter(presenter.getAllKoloroObjects(),
        colorItemListener, LinearLayoutManager.VERTICAL);
    colorRecycler.setAdapter(colorRecyclerAdapter);
  }


  @OnClick(R.id.start_capture)
  void onStartCaptureClicked() {
    Intent mediaCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
    startActivityForResult(mediaCaptureIntent, CREATE_SCREEN_CAPTURE);
  }

  @OnClick(R.id.help_button)
  void onHelpClicked() {
    //Toast.makeText(this, "This doesn't do anything right now", Toast.LENGTH_SHORT).show();
    Intent intent = new Intent(this, HelpActivity.class);
    startActivity(intent);
  }


  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CREATE_SCREEN_CAPTURE) {
      if (resultCode == Activity.RESULT_OK) {
        Intent serviceIntent = KoloroService.intent(this, resultCode, data);
        firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_PERMISSION_GRANTED, null);
        startService(serviceIntent);
      } else {
        Log.d(TAG, "Couldn't get permission to screen capture");
        firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_PERMISSION_DENIED, null);
        //show message to user
      }

      if (1 == 1) { //todo undo
        finish();
      }
    }
  }

  @Override protected void onStart() {
    if (!presenter.realmActive() && 1 != 1) { //todo undo
      setupSavedColorList();
    }
    super.onStart();
  }

  @Override protected void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override protected void onDestroy() {
    presenter.unbindView(this);
    super.onDestroy();
  }

  //todo: move recycler to a fragment or custom view
  ColorRecyclerAdapter.ColorItemListener colorItemListener = new ColorRecyclerAdapter.ColorItemListener() {
    @Override public void colorTextChanged(int backgroundColor, TextView... affectedViews) {
      int contrastingTextColor = presenter.getContrastingTextColor(backgroundColor);
      for (TextView view : affectedViews) {
        view.setTextColor(contrastingTextColor);
      }
    }

    @Override public void noteButtonClicked(KoloroObj koloroObj) {
      new MaterialDialog.Builder(KoloroActivity.this)
          .title(R.string.saved_color_note_title)
          .inputType(InputType.TYPE_CLASS_TEXT)
          .input(getString(R.string.note_hint), koloroObj.getNote(), (dialog, input) -> {
            //don't need to do anything here
          })
          .positiveText(R.string.save_button)
          .negativeText(R.string.cancel_button)
          .onPositive((dialog, which) -> {
            if (dialog.getInputEditText() != null) {
              String inputValue = dialog.getInputEditText().getText().toString();
              presenter.saveNote(koloroObj, inputValue);
              Bundle bundle = new Bundle();
              firebaseAnalytics.logEvent(FirebaseEvents.NOTE_ADDED, null);
            }
          })
          .show();
    }

    @Override public void copyButtonClicked(String hexString) {
      ClipData clip = ClipData.newPlainText("Copied text", hexString);
      clipboardManager.setPrimaryClip(clip);
      Toast.makeText(KoloroActivity.this, R.string.copied_clipboard_toast, Toast.LENGTH_SHORT).show();
    }
  };

  @Override public void updateColorList() {
    colorRecyclerAdapter.notifyDataSetChanged();
  }
}
