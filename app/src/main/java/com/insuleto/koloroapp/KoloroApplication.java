package com.insuleto.koloroapp;

import android.app.Application;
import android.content.Context;

public class KoloroApplication extends Application {

  private ApplicationComponent applicationComponent;

  public static KoloroApplication get(Context context) {
    return (KoloroApplication) context.getApplicationContext();
  }

  @Override public void onCreate() {
    super.onCreate();

    applicationComponent = prepareApplicationComponent().build();
    applicationComponent.inject(this);
  }

  protected com.insuleto.koloroapp.DaggerApplicationComponent.Builder prepareApplicationComponent() {
    return com.insuleto.koloroapp.DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this));
  }

  public ApplicationComponent applicationComponent() {
    return applicationComponent;
  }
}
