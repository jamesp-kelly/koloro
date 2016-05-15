package com.jameskelly.koloro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func0;

public class ScreenCaptureManager {

  private static final String TAG = ScreenCaptureManager.class.getSimpleName();
  private static final String VIRTUAL_DISPLAY_NAME = "com.jameskelly.kolora";

  private final DateFormat capture_format = new SimpleDateFormat("'Koloro_'yyyy-MM-dd-HH-mm-ss'.jpg'",
      Locale.CANADA);
  private final Context context;
  private final int resultCode;
  private final Intent resultData;
  private ScreenInfo screenInfo;
  private ImageReader imageReader;
  private VirtualDisplay virtualDisplay;
  private Surface surface;
  private MediaProjection mediaProjection;

  @Inject MediaProjectionManager mediaProjectionManager;
  @Inject WindowManager windowManager;

  public ScreenCaptureManager(Context context, int resultCode, Intent resultData) {
    this.context = context;
    this.resultCode = resultCode;
    this.resultData = resultData;
    KoloroApplication.get(context).applicationComponent().inject(this);
  }

  public void captureCurrentScreen(ImageCaptureListener imageCaptureListener) {
    nativeScreenCapture(imageCaptureListener);
  }

  private void nativeScreenCapture(ImageCaptureListener imageCaptureListener) {

    ScreenInfo screenInfo = getDeviceScreenInfo();

    mediaProjection =
        mediaProjectionManager.getMediaProjection(resultCode, resultData);
    imageReader = ImageReader.newInstance(screenInfo.width, screenInfo.height, PixelFormat.RGBA_8888, 2);
    surface = imageReader.getSurface();
    virtualDisplay = mediaProjection.createVirtualDisplay(VIRTUAL_DISPLAY_NAME,
        screenInfo.width, screenInfo.height, screenInfo.density,
        DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION, surface, null, null);

    imageReader.setOnImageAvailableListener(reader -> {
      Image image = reader.acquireLatestImage();
      if (image != null) {
        Bitmap capturedBitmap = null;

        try {
          Image.Plane[] planes = image.getPlanes();
          ByteBuffer buffer = planes[0].getBuffer();
          int pixelStride = planes[0].getPixelStride();
          int rowStride = planes[0].getRowStride();
          int rowPadding = rowStride - pixelStride * screenInfo.width;

          capturedBitmap =
              Bitmap.createBitmap(screenInfo.width + rowPadding / pixelStride, screenInfo.height,
                  Bitmap.Config.ARGB_8888);
          capturedBitmap.copyPixelsFromBuffer(buffer);
          //crop
          capturedBitmap = Bitmap.createBitmap(capturedBitmap, 0, 0, screenInfo.width, screenInfo.height);

          imageCaptureListener.onImageCaptured(capturedBitmap);

        } catch (UnsupportedOperationException e) {
          Log.e(TAG, "Native screen capture failed. Use canvas mode", e);
          //todo call canvas method here
        } finally {
          image.close();
          reader.close();
          virtualDisplay.release();
          mediaProjection.stop();
        }
      }
    }, null);
  }


  public interface ImageCaptureListener {
    void onImageCaptured(Bitmap capturedImage);
    void onImageCaptureError();
  }

  private ScreenInfo getDeviceScreenInfo() {
    DisplayMetrics metrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getRealMetrics(metrics);
    return new ScreenInfo(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
  }

  static final class ScreenInfo {
    final int width;
    final int height;
    final int density;

    public ScreenInfo(int width, int height, int density) {
      this.width = width;
      this.height = height;
      this.density = density;
    }
  }

  public Uri saveBitamp(final Bitmap bitmap) throws IOException {

    File galleryRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    File koloroDir = new File(galleryRoot, "Koloro");

    if (!koloroDir.exists() && !koloroDir.mkdir()) {
      Log.e(TAG, "Unable to create directory");
    }

    String captureFileName = capture_format.format(new Date());
    File captureFile = new File(koloroDir, captureFileName);
    FileOutputStream fos = null;

    fos = new FileOutputStream(captureFile.getAbsolutePath());
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

    return Uri.fromFile(captureFile);
  }

  public Observable<Uri> getSavedBitmapUriObservable(final Bitmap bitmap) {
    return Observable.defer(new Func0<Observable<Uri>>() {
      @Override public Observable<Uri> call() {
        try {
          return Observable.just(saveBitamp(bitmap));
        } catch (IOException e) {
          return Observable.error(e);
        }
      }
    });
  }
}
