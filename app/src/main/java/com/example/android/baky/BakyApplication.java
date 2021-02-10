package com.example.android.baky;

import android.app.Application;

import timber.log.Timber;

public class BakyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Timber.plant(new Timber.DebugTree());
  }
}
