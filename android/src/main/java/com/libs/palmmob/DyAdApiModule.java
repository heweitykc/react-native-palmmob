
package com.libs.palmmob;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.duoyou.task.openapi.DyAdApi;
import com.duoyou.task.openapi.OnHttpCallback;
import com.duoyou.task.openapi.TaskListParams;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.libs.palmmob.oaid.DeviceIdUtils;
import com.libs.palmmob.oaid.MiitHelper;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class DyAdApiModule extends ReactContextBaseJavaModule {

  public static Promise getDeviceIDPromise;

  private final ReactApplicationContext reactContext;

  public DyAdApiModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "DyAdApi";
  }

  @ReactMethod
  public void init(String mediaid, String secret, String channel, final Promise promise) {
    DyAdApi.getDyAdApi().init(reactContext, mediaid, secret, channel);
  }

  @ReactMethod
  public void jumpAdList(String userId, int advertType, final Promise promise) {
    DyAdApi.getDyAdApi().jumpAdList(reactContext, userId, advertType);
  }

  @ReactMethod
  public void jumpAdDetail(String userId, String advertType, final Promise promise) {
    DyAdApi.getDyAdApi().jumpAdDetail(reactContext, userId, advertType);
  }

  @ReactMethod
  public void setTitleBarColor(int color, final Promise promise) {
    DyAdApi.getDyAdApi().setTitleBarColor(color);
  }

  @ReactMethod
  public void setTitle(String title,  final Promise promise) {
    DyAdApi.getDyAdApi().setTitle(title);
  }

  @ReactMethod
  public void jumpMine(String userId, final Promise promise) {
    DyAdApi.getDyAdApi().jumpMine(reactContext, userId);
  }

  @ReactMethod
  public void getAdListFragment(String userId, int advertType, final Promise promise) {
    DyAdApi.getDyAdApi().getAdListFragment(userId, advertType);
  }

  @ReactMethod
  public void getTaskList(String userId, String p_type, int p_page, int p_size, String  p_extra, final Promise promise) {
    TaskListParams listParams = new TaskListParams();
    listParams.type = p_type;
    listParams.page = p_page;
    listParams.size = p_size;
    listParams.extra = p_extra;
    DyAdApi.getDyAdApi().getTaskList(userId, listParams, new OnHttpCallback() {

      @Override
      public void onSuccess(JSONArray jsonArray) {
        promise.resolve(jsonArray.toString());
      }

      @Override
      public void onFailure(String code, String errmsg) {
        final Map<String, Object> ret = new HashMap<>();
        ret.put("String", code);
        ret.put("String", errmsg);
        promise.resolve(ret);
      }

    });
  }


}