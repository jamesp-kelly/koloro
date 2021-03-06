package com.insuleto.koloroapp;

import android.app.Application;
import android.content.Context;
import com.insuleto.koloroapp.repository.KoloroRepository;
import javax.inject.Inject;

public class KoloroApplication extends Application {

  private ApplicationComponent applicationComponent;

  @Inject KoloroRepository koloroRepository;

  public static KoloroApplication get(Context context) {
    return (KoloroApplication) context.getApplicationContext();
  }

  @Override public void onCreate() {
    super.onCreate();

    //LeakCanary.install(this);

    applicationComponent = prepareApplicationComponent().build();
    applicationComponent.inject(this);

    koloroRepository.setupConnection(this);
  }

  //public void mustDie(Object object) {
  //  if (refWatcher != null) {
  //    refWatcher.watch(object);
  //  }
  //}

  protected com.insuleto.koloroapp.DaggerApplicationComponent.Builder prepareApplicationComponent() {
    return com.insuleto.koloroapp.DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this));
  }

  public ApplicationComponent applicationComponent() {
    return applicationComponent;
  }
}
