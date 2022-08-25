package com.libs.palmmob.updater;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.palmmob3.globallibs.updater.GlobalUpdater;


public class UpdaterModule extends ReactContextBaseJavaModule {

    ReactApplicationContext reactContext;

    @NonNull
    @Override
    public String getName() {
        return "UpdaterModule";
    }

    public UpdaterModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
    }

    @ReactMethod
    public void goUpdate(String updateTitle, String updateContent, String channel) {
        Activity activity = this.reactContext.getCurrentActivity();
        GlobalUpdater.getInstance().goUpdate(
                activity, updateTitle, updateContent, channel
        );
    }
}
