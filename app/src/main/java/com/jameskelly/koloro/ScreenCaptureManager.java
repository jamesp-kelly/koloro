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
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.inject.Inject;

public class ScreenCaptureManager {

  private static final String TAG = ScreenCaptureManager.class.getSimpleName();
  private static final String VIRTUAL_DISPLAY_NAME = "com.jameskelly.kolora";

  private final DateFormat capture_format = new SimpleDateFormat("'Koloro_'yyyy-MM-dd-HH-mm-ss'.jpg'",
      Locale.CANADA);
  private final Context context;
  private final int resultCode;
  private final Intent resultData;
  private final ImageCaptureListener imageCaptureListener;
  private ScreenInfo screenInfo;
  private ImageReader imageReader;
  private VirtualDisplay virtualDisplay;
  private Surface surface;
  private MediaProjection mediaProjection;

  @Inject MediaProjectionManager mediaProjectionManager;
  @Inject WindowManager windowManager;

  public ScreenCaptureManager(Context context, int resultCode, Intent resultData, ImageCaptureListener imageCaptureListener) {
    this.context = context;
    this.resultCode = resultCode;
    this.resultData = resultData;
    this.imageCaptureListener = imageCaptureListener;
    KoloroApplication.get(context).applicationComponent().inject(this);
  }

  public void captureScreen() {

    File galleryRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    File koloroDir = new File(galleryRoot, "Koloro");

    if (!koloroDir.exists() && !koloroDir.mkdir()) {
      Log.e(TAG, "Unable to create directory");
    }

    if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
      String test = "";
    }

    String captureFileName = capture_format.format(new Date());
    File captureFile = new File(koloroDir, captureFileName);
    screenInfo = getDeviceScreenInfo();

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
          convertImage(image, captureFile);
        }
    }, null);
  }

  private void convertImage(Image image, File captureFile) {

    FileOutputStream fos = null;
    Bitmap bitmap = null;

    try {
      Image.Plane[] planes = image.getPlanes();
      ByteBuffer buffer = planes[0].getBuffer();
      int pixelStride = planes[0].getPixelStride();
      int rowStride = planes[0].getRowStride();
      int rowPadding = rowStride - pixelStride * screenInfo.width;

      bitmap = Bitmap.createBitmap(screenInfo.width + rowPadding/pixelStride, screenInfo.height, Bitmap.Config.ARGB_8888);
      bitmap.copyPixelsFromBuffer(buffer);

      //crop
      bitmap = Bitmap.createBitmap(bitmap, 0, 0, screenInfo.width, screenInfo.height);

      fos = new FileOutputStream(captureFile.getAbsolutePath());
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

      imageCaptureListener.onImageCaptured(Uri.fromFile(captureFile));

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      imageCaptureListener.onImageCaptureError();
    } finally {
      imageReader.close();
      surface.release();
      virtualDisplay.release();
      mediaProjection.stop();
      if (bitmap != null && !bitmap.isRecycled()) {
        bitmap.recycle();
      }
    }
  }

  public interface ImageCaptureListener {
    void onImageCaptured(Uri captureUri);
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
}
