package com.libs.palmmob.admob;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.palmmob3.globallibs.ad.admob.AdMob;


public class AdMobModule extends ReactContextBaseJavaModule {

    ReactApplicationContext reactContext;
    AdMob admob;

    @NonNull
    @Override
    public String getName() {
        return "AdMobModule";
    }

    public AdMobModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
    }

    @ReactMethod
    public void init(Promise promise) {
        admob = new AdMob();
        admob.init(this.reactContext);
        promise.resolve(true);
    }

    @ReactMethod
    public void showReward(String adid, Promise promise) {

        reactContext.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
                admob.loadReward(reactContext.getCurrentActivity(), adid, new AdMob.AdListener() {
                    @Override
                    public void onErr(int code, String msg) {
                        WritableMap params = Arguments.createMap();
                        params.putInt("code", code);
                        params.putString("msg",  msg);

                        promise.resolve(params);
                    }

                    @Override
                    public void onFinish(int code, String msg) {
                        WritableMap params = Arguments.createMap();
                        params.putInt("code", code);
                        params.putString("msg",  msg);

                        promise.resolve(params);
                    }
                });
            }
        });

    }
}
