
package com.reactlibrary;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.text.TextUtils;
import android.content.Intent;
import android.content.ComponentName;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class RNReactNativePalmmobModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNReactNativePalmmobModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNReactNativePalmmob";
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


//  @ReactMethod
//  public String getCopyData(){
//    ClipboardManager clip = (ClipboardManager)getReactApplicationContext().getCurrentActivity().getSystemService(reactContext.CLIPBOARD_SERVICE);
//    String text = clip.getText().toString();
//    if(text == null){
//      text = "";
//    }
//    return text;
//  }

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
  public String openScanner() {
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

}