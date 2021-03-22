
package com.libs.palmmob;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.libs.palmmob.oaid.DeviceIdUtils;
import com.libs.palmmob.oaid.MiitHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class OAIDUtilModule extends ReactContextBaseJavaModule {

  public static Promise getDeviceIDPromise;

  private final ReactApplicationContext reactContext;

  public OAIDUtilModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "OAIDUtil";
  }

  @ReactMethod
  public void getDeviceID(final Promise promise) {
    OAIDUtilModule.getDeviceIDPromise = promise;
    int ANDROID_Q = 29;
    if (Build.VERSION.SDK_INT >= ANDROID_Q) {
      this.initOaid();
    } else {
      this.initImei();
    }
  }

  private void initOaid() {
    MiitHelper miitHelper = new MiitHelper(new MiitHelper.AppIdsUpdater() {
      @Override
      public void OnIdsAvalid(@NonNull final String oaid) {
        OAIDUtilModule.getDeviceIDPromise.resolve(oaid);
      }
    });
    Log.i("json", "oaid = 获取OAID");
    miitHelper.getDeviceIds(this.reactContext);
  }

  private void initImei() {
    HashSet<String> set = DeviceIdUtils.getDeviceIds(this.reactContext);
    StringBuffer sb = new StringBuffer();
    Iterator<String> iterator = set.iterator();
    while(iterator.hasNext()) {
      String imei = iterator.next();
      sb.append(imei).append("\n");
    }
    OAIDUtilModule.getDeviceIDPromise.resolve(sb.toString());
  }


}