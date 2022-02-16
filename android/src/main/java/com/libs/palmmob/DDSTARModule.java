package com.libs.palmmob;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.text.TextUtils;
import android.content.Intent;
import android.content.ComponentName;


import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.facebook.react.common.build.ReactBuildConfig;
import com.umeng.commonsdk.UMConfigure;

public class DDSTARModule extends ReactContextBaseJavaModule {

  static final String CHANNEL_KEY = "APP_CHANNEL";
    static final String APP_AREA = "APP_AREA";
  static final String UMAPP_KEY = "UM_APPKEY";

  private final ReactApplicationContext reactContext;

  public DDSTARModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "DDSTARModule";
  }

/**
   * 这里返回的值会被JS模块当做常量来使用
   * 使用方式为
   *
   * NativeModules.RNToast.SHORT === Toast.LENGTH_SHORT
   * NativeModules.RNToast.LONG === Toast.LENGTH_LONG
   *
   * @return
   */
  @Nullable
  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    constants.put("SHORT", Toast.LENGTH_SHORT);
    constants.put("LONG", Toast.LENGTH_LONG);

    return  constants;
  }

  /**
   * 这里暴露一个方法给 React Native
   *
   * 在JS中使用方式为：
   *
   * NativeModules.RNToast.show(msg, duration); // duration 可以使用上面 getConstants 方法暴露出来的常量
   *
   * @param msg
   * @param duration
   */
  @ReactMethod
  public void show( String msg, int duration ){
    Toast.makeText(getReactApplicationContext(), msg, duration).show();
  }

  @ReactMethod
  public void getCallState(final Promise promise) {
    try {
      TelephonyManager mTelephonyManager = (TelephonyManager) this.reactContext.getSystemService(Context.TELEPHONY_SERVICE);
      int state = mTelephonyManager.getCallState();
      promise.resolve(state);
      return;
    } catch (Exception e) {
      e.printStackTrace();
    }
    promise.resolve(0);
  }

  @ReactMethod
  public String getCopyData(){
    Activity currentActivity = getReactApplicationContext().getCurrentActivity();
    ClipboardManager manager = (ClipboardManager) currentActivity.getSystemService(reactContext.CLIPBOARD_SERVICE);
    if (manager != null) {
      if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
        CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
        String addedTextString = String.valueOf(addedText);
        if (!TextUtils.isEmpty(addedTextString)) {
          return addedTextString;
        }
      }
    }
    return "";
  }

  @ReactMethod
  public String openWXScan() {
    try {
      Intent intent = new Intent();
      intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
      intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
      intent.setFlags(335544320);
      intent.setAction("android.intent.action.VIEW");
      reactContext.startActivity(intent);
    } catch (Exception e) {
      return e.getMessage();
    }
    return "OK";
  }

  private PackageInfo getPackageInfo() throws Exception {
    return getReactApplicationContext().getPackageManager().getPackageInfo(getReactApplicationContext().getPackageName(), 0);
  }

  @ReactMethod
  public void getAppChannel(final Promise promise) {
    String ch = DDSTARModule.getMetaVal(this.reactContext, DDSTARModule.CHANNEL_KEY);
    promise.resolve(ch);
    return;
  }

  @ReactMethod
  public void getAppName(final Promise promise) {
    String appName = "unknown";
    try {
      appName = getReactApplicationContext().getApplicationInfo().loadLabel(getReactApplicationContext().getPackageManager()).toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    promise.resolve(appName);
    return;
  }

  @ReactMethod
  public void getAppBuild(final Promise promise) {
    String buildNumber = "unknown";
    try {
      buildNumber = Integer.toString(getPackageInfo().versionCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
    promise.resolve(buildNumber);
    return;
  }

  @ReactMethod
  public void getAppArea(final Promise promise) {
    String ch = DDSTARModule.getMetaVal(this.reactContext, DDSTARModule.APP_AREA);
    promise.resolve(ch);
    return;
  }

  @ReactMethod
  public void initUM(String pushSecret, final Promise promise) {
    String channel = DDSTARModule.getMetaVal(this.reactContext, DDSTARModule.CHANNEL_KEY);
    String appkey = DDSTARModule.getMetaVal(this.reactContext, DDSTARModule.UMAPP_KEY);
    UMConfigure.init(reactContext, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, pushSecret);
    UMConfigure.setLogEnabled(ReactBuildConfig.DEBUG);
    promise.resolve(0);
  }

  static public void preInitUM(Context context){
    String appkey = DDSTARModule.getMetaVal(context, DDSTARModule.UMAPP_KEY);
    String channel = DDSTARModule.getMetaVal(context, DDSTARModule.CHANNEL_KEY);
    UMConfigure.preInit(context, appkey, channel);
  }

  static public String getMetaVal(final Context context, final String key) {
    String ch;
    try {
      ApplicationInfo ai = null;
      ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      ch = ai.metaData.getString(key);
    } catch (PackageManager.NameNotFoundException e) {
      //e.printStackTrace();
      ch = "other";
    }
    return ch;
  }
}