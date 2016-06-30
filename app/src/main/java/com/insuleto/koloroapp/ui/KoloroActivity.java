package com.insuleto.koloroapp.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
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
import com.insuleto.koloroapp.KoloroApplication;
import com.insuleto.koloroapp.R;
import com.insuleto.koloroapp.model.ColorFormat;
import com.insuleto.koloroapp.model.InAppBilling;
import com.insuleto.koloroapp.model.KoloroObj;
import com.insuleto.koloroapp.preferences.BooleanPreference;
import com.insuleto.koloroapp.preferences.PreferencesModule;
import com.insuleto.koloroapp.service.KoloroService;
import com.insuleto.koloroapp.ui.adaptors.ColorRecyclerAdapter;
import com.insuleto.koloroapp.ui.presenters.KoloroPresenter;
import com.insuleto.koloroapp.ui.views.KoloroView;
import com.insuleto.koloroapp.util.FirebaseEvents;
import com.insuleto.koloroapp.util.iap.IabBroadcastReceiver;
import com.insuleto.koloroapp.util.iap.IabHelper;
import com.insuleto.koloroapp.util.iap.IabResult;
import com.insuleto.koloroapp.util.iap.Inventory;
import com.insuleto.koloroapp.util.iap.Purchase;
import javax.inject.Inject;
import javax.inject.Named;

public class KoloroActivity extends BaseActivity implements KoloroView, PreferenceFragment.PreferenceChangeListener,
    IabBroadcastReceiver.IabBroadcastListener {

  private ColorRecyclerAdapter colorRecyclerAdapter;
  private boolean cameFromOverlay = false;
  private boolean isPremium = false;
  private IabHelper billingHelper;
  private BroadcastReceiver billingBroadcastReceiver;
  private PreferenceFragment preferenceFragment;

  @Inject MediaProjectionManager mediaProjectionManager;
  @Inject KoloroPresenter presenter;
  @Inject FirebaseAnalytics firebaseAnalytics;
  @Inject ClipboardManager clipboardManager;

  @Inject @Named(PreferencesModule.QUICK_LAUNCH_KEY) Boolean quickLaunchActive;
  @Inject @Named(PreferencesModule.COLOR_FORMAT_KEY) int colorFormat;
  @Inject @Named(PreferencesModule.PREMIUM_ENABLED_KEY) BooleanPreference premiumEnabledPreference;

  @BindDimen(R.dimen.prefs_layout_margin_top) int prefsLayoutMarginTop;
  @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
  @BindView(R.id.prefs_layout) ViewGroup prefsLayout;
  @BindView(R.id.start_capture) Button startCaptureButton;
  @BindView(R.id.adView) AdView adView;
  @BindView(R.id.color_list_recycler) RecyclerView colorRecycler;
  @BindView(R.id.upgrade_button) Button upgradeButton;

  @Override protected void onCreate(Bundle savedInstanceState) {

    KoloroApplication.get(this).applicationComponent().inject(this);
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();

    cameFromOverlay = ((intent.getFlags() & Intent.FLAG_ACTIVITY_NO_USER_ACTION) != 0);

    if (mediaProjectionManager != null && quickLaunchActive && !cameFromOverlay) {
      setTheme(R.style.AppTheme_Translucent);
      Intent mediaCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
      startActivityForResult(mediaCaptureIntent, CREATE_SCREEN_CAPTURE);
    } else {
      setContentView(R.layout.activity_koloro);
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
      ButterKnife.bind(this);
      presenter.bindView(this);

      setupSavedColorList();
      cameFromOverlay = false;

      billingHelper = new IabHelper(this, getPublicApplicationKey());
      billingHelper.enableDebugLogging(true);

      billingHelper.startSetup(result -> {
        Log.d(TAG, "onIabSetupFinished");

        if (billingHelper != null && result.isSuccess()) {
          billingBroadcastReceiver = new IabBroadcastReceiver(KoloroActivity.this);
          registerReceiver(billingBroadcastReceiver, new IntentFilter(IabBroadcastReceiver.ACTION));

          try {
            billingHelper.queryInventoryAsync(inventoryCheckFinishedListener);
          } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG, "Error checking inventory. Another async operation is progress");
          }
        } else if (result.isFailure()) {
          Log.e(TAG, "Billing setup result failed: " + result);
        }
      });

      preferenceFragment = new PreferenceFragment();

      getSupportFragmentManager().beginTransaction()
          .replace(R.id.prefs_layout, preferenceFragment)
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
    colorRecyclerAdapter = new ColorRecyclerAdapter(this, presenter.getAllKoloroObjects(),
        colorItemListener, (colorFormat == ColorFormat.HEX));
    colorRecycler.setAdapter(colorRecyclerAdapter);
  }


  @OnClick(R.id.start_capture)
  void onStartCaptureClicked() {
    Intent mediaCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
    startActivityForResult(mediaCaptureIntent, CREATE_SCREEN_CAPTURE);
  }

  @OnClick(R.id.help_button)
  void onHelpClicked() {
    startActivity(new Intent(this, HelpActivity.class));
  }

  @OnClick(R.id.upgrade_button)
  void onUpgradeClicked() {
    try {
      billingHelper.launchPurchaseFlow(this, InAppBilling.TEST_PURCHASED, InAppBilling.PREMIUM_RC,
          purchaseFinishedListener);
    } catch (IabHelper.IabAsyncInProgressException e) {
      Log.e(TAG, "Error launching purchase flow.Another async operation is progress.");
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CREATE_SCREEN_CAPTURE) {
      if (resultCode == Activity.RESULT_OK && !KoloroService.isServiceRunning(this)) {
        Intent serviceIntent = KoloroService.intent(this, resultCode, data);
        firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_PERMISSION_GRANTED, null);
        startService(serviceIntent);
      } else if (resultCode == Activity.RESULT_OK) {
        Toast.makeText(this, R.string.overlay_already_launched_message, Toast.LENGTH_SHORT).show();
      } else {
        Log.d(TAG, "Couldn't get permission to screen capture");
        firebaseAnalytics.logEvent(FirebaseEvents.CAPTURE_PERMISSION_DENIED, null);
        //show message to user
      }

      if (quickLaunchActive) {
        finish();
      }
    } else if (requestCode == InAppBilling.PREMIUM_RC) {
      billingHelper.handleActivityResult(InAppBilling.PREMIUM_RC, resultCode, data);
    }
}

  @Override protected void onStart() {
    if (!presenter.realmActive() && (!quickLaunchActive || cameFromOverlay)) {
      setupSavedColorList();
    }
    super.onStart();
  }

  @Override protected void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    if (presenter != null) {
      presenter.unbindView(this);
    }

    if (billingBroadcastReceiver != null) {
      unregisterReceiver(billingBroadcastReceiver);
    }

    if (billingHelper != null) {
      billingHelper.disposeWhenFinished();
      billingHelper = null;
    }
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

    @Override public void copyButtonClicked(KoloroObj koloroObj) {

      String colorString;
      if (colorFormat == ColorFormat.HEX) {
        colorString = koloroObj.getHexString();
      } else {
        colorString = koloroObj.getRgbString();
      }

      ClipData clip = ClipData.newPlainText("Copied text", colorString);
      clipboardManager.setPrimaryClip(clip);
      Toast.makeText(KoloroActivity.this, R.string.copied_clipboard_toast, Toast.LENGTH_SHORT).show();
    }

    @Override public void preferencesMenuItemClicked() {
      Toast.makeText(KoloroActivity.this, "Already there...", Toast.LENGTH_SHORT).show();
    }

    @Override public void shareMenuItemClicked() {
      Toast.makeText(KoloroActivity.this, "test", Toast.LENGTH_SHORT).show();
    }

    @Override public void clearMenuItemClicked() {
      presenter.removeAllKoloroObjects();
    }
  };

  @Override public void updateColorList() {
    colorRecyclerAdapter.notifyDataSetChanged();
  }

  @Override public void intValueChanged(String preferenceKey, int newValue) {
    if (preferenceKey.equals(PreferencesModule.COLOR_FORMAT_KEY)) {
      colorRecyclerAdapter = new ColorRecyclerAdapter(this, presenter.getAllKoloroObjects(),
          colorItemListener, (newValue == ColorFormat.HEX));
      colorRecycler.setAdapter(colorRecyclerAdapter);
    }
  }

  @Override public void boolValueChanged(String preferenceKey, boolean newValue) {
  }

  private String getPublicApplicationKey() {
    //return String.format("%s%s%s%s",
    //    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwCTe2rX85BpEVPANRG8i3crVC+OckrD9cxWnpPKGr7ZN33en5xKo6vL/lUMF2n3Woudy5swXJq0rsAKci",
    //    "WwBX9YPNKn56mjlBpZrb7wc79jIgLATga4brxbFzfgcwZB7NLi8NhOC1uczO5uhlx02LcgdpBMCYtiU2SbVu643H1sAEanFHU/q/8k3uIcIaHsWUDACZ1LBubmPdP",
    //    "sR1ED0M7OiUEe7GiT9jP9Gc4s9heTCNo6pdKE6Jkj514PRTwpBOy8NrcRtakgVl8mGC2TRtOSjeeM5wN6LXfahufyTJOfC/mm7R6UyWli/B+GQNeIS4B5iP0hWrLA",
    //    "GMn2jYczFywIDAQAB");

    return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwCTe2rX85BpEVPANRG8i3crVC+OckrD9cxWnpPKGr7ZN33en5xKo6vL/lUMF2n3Woudy5swXJq0rsAKciWwBX9YPNKn56mjlBpZrb7wc79jIgLATga4brxbFzfgcwZB7NLi8NhOC1uczO5uhlx02LcgdpBMCYtiU2SbVu643H1sAEanFHU/q/8k3uIcIaHsWUDACZ1LBubmPdPsR1ED0M7OiUEe7GiT9jP9Gc4s9heTCNo6pdKE6Jkj514PRTwpBOy8NrcRtakgVl8mGC2TRtOSjeeM5wN6LXfahufyTJOfC/mm7R6UyWli/B+GQNeIS4B5iP0hWrLAGMn2jYczFywIDAQAB";
  }

  @Override public void receivedBroadcast() {
    Log.d(TAG, "Received broadcast. Checking inventory");
    try {
      billingHelper.queryInventoryAsync(inventoryCheckFinishedListener);
    } catch (IabHelper.IabAsyncInProgressException e) {
      Log.e(TAG, "Error checking inventory. Another async operation in progress");
    }
  }

  IabHelper.QueryInventoryFinishedListener inventoryCheckFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
    @Override public void onQueryInventoryFinished(IabResult result, Inventory inv) {
      if (billingHelper != null && result.isSuccess()) {
        Log.d(TAG, "Inventory check successful");

        Purchase premiumPurchase = inv.getPurchase(InAppBilling.TEST_PURCHASED);
        isPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
        Log.i(TAG, "inventoryCheckFinishedListener isPremium = " + isPremium);

        if (isPremium != premiumEnabledPreference.get()) {
          premiumEnabledPreference.set(isPremium);
        }

        //TODO: REMOVE TEST CONSUME CODE
        if (isPremium) { //consume purchase so can test again
          try {
            billingHelper.consumeAsync(premiumPurchase, new IabHelper.OnConsumeFinishedListener() {
              @Override public void onConsumeFinished(Purchase purchase, IabResult result) {
                Toast.makeText(KoloroActivity.this, "consumed test purchase", Toast.LENGTH_SHORT).show();
                isPremium = false;
              }
            });
          } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
          }
        }
        //TODO END TEST CODE


      } else if (result.isFailure()) {
        Log.e(TAG, "Inventory check failed: " + result);
      }

      premiumEnabledPreference.set(isPremium);
      updateUiForPremium();
    }
  };


  IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
    @Override public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
      Log.d(TAG, "onIabPurchaseFinished");
      if (billingHelper != null && result.isSuccess()) {
        if (purchase.getSku().equals(InAppBilling.TEST_PURCHASED)) {
          Toast.makeText(KoloroActivity.this, "You have upgraded to premium", Toast.LENGTH_SHORT).show();
          isPremium = true;
        }
      } else if (result.isFailure()) {
        Log.e(TAG, "Error purchasing: " + result);
        Toast.makeText(KoloroActivity.this, "Error purchasing", Toast.LENGTH_SHORT).show();
      }

      premiumEnabledPreference.set(isPremium);

      updateUiForPremium();
    }
  };

  private void updateUiForPremium() {
    upgradeButton.setVisibility(isPremium ? View.GONE : View.VISIBLE);
    preferenceFragment.updateBillingUI(isPremium);
  }

  boolean verifyDeveloperPayload(Purchase p) {
    String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

    return true;
  }
}
