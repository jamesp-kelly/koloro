package com.insuleto.koloroapp.ui.presenters;

import android.graphics.Color;
import android.os.Environment;
import com.insuleto.koloroapp.events.ImageProcessedEvent;
import com.insuleto.koloroapp.model.KoloroObj;
import com.insuleto.koloroapp.model.RgbColor;
import com.insuleto.koloroapp.repository.KoloroRepository;
import com.insuleto.koloroapp.ui.views.ColorPickerView;
import io.realm.RealmResults;
import java.io.File;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ColorPickPresenterImpl implements ColorPickerPresenter {

  private KoloroRepository repository;
  private ColorPickerView view;
  private RealmResults<KoloroObj> koloroObjects;

  public ColorPickPresenterImpl(KoloroRepository repository) {
    this.repository = repository;
  }

  @Override public void bindView(ColorPickerView view) {
    this.view = view;
  }

  @Override public void unbindView(ColorPickerView view) {
    this.view = null;
  }

  @Override public void onStart() {
    EventBus.getDefault().register(this);
  }

  @Override public void onStop() {
    EventBus.getDefault().unregister(this);
    repository.closeConnection();
  }

  @Override public void setupRealm() {
    //todo delete
    //repository.setupConnection();
  }

  @Override public List<KoloroObj> getAllKoloroObjects() {
    koloroObjects = repository.getAllKoloroObjs();
    koloroObjects.addChangeListener(() -> view.updateColorList());
    return koloroObjects;
  }

  @Override public void removeAllKoloroObjects() {
    repository.deleteAllKoloroObjects();
    view.updateColorList();
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  @Override public void onImageProcessedEventReceived(ImageProcessedEvent event) {
    if (event.isSuccess() && event.getImageUri() != null) {
      event.getCaptureToast().cancel();
      view.displayCaptureImage(event.getImageUri());
    }
  }

  @Override public String generateHexColor(int color) {
    int r = Color.red(color);
    int g = Color.green(color);
    int b = Color.blue(color);

    return String.format("#%02x%02x%02x", r, g, b);
  }

  @Override public RgbColor generateRgbColor(int color) {
    return new RgbColor(Color.red(color), Color.green(color), Color.blue(color));
  }

  @Override public int getContrastingTextColor(int backgroundColor) {
    int resultColorValue = 0;

    int r = Color.red(backgroundColor);
    int g = Color.green(backgroundColor);
    int b = Color.blue(backgroundColor);

    double backgroundBrightness = 1 - (0.299 * r + 0.587 * g + 0.114 * b)/255;

    if (backgroundBrightness < 0.5) {
      //bright color, use black
      resultColorValue = 0;
    } else {
      resultColorValue = 255;
    }

    return Color.argb(255, resultColorValue, resultColorValue, resultColorValue);
  }

  @Override public void saveColor(int currentlySelectedColor, String currentlySelectedColorHex) {
    KoloroObj koloroObj =
        repository.createKoloroObj(currentlySelectedColor, currentlySelectedColorHex);
  }

  @Override public void saveNote(KoloroObj koloroObj, String inputValue) {
    repository.updateNote(koloroObj, inputValue);
  }

  @Override public void removeStoredScreenShot() { //move to screencapturemanager?
    //remove whole koloro dir
    File galleryRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    File koloroDir = new File(galleryRoot, "Koloro");

    if (koloroDir.exists()) {
      delete(koloroDir);
    }
  }

  private void delete(File toDelete) {
    if (toDelete.isDirectory()) {
      for (File child : toDelete.listFiles()) {
        delete(child);
      }
    }

    toDelete.delete();
  }
}
