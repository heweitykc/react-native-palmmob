package com.libs.palmmob;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.duoyou.task.openapi.DyAdApi;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.hailong.appupdate.AppUpdateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ContactManager.ContactListener;
import com.libs.palmmob.speech.util.FucUtil;
import com.libs.palmmob.speech.util.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class XFYunLATModule extends ReactContextBaseJavaModule {

  private static String TAG = "XFYunLATModule";


  private final ReactApplicationContext reactContext;
  private String resultType = "json";
  private boolean cyclic = false;//音频流识别是否循环调用

  private SpeechRecognizer mIat;

  private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
  private StringBuffer buffer = new StringBuffer();

  int ret = 0; // 函数调用返回值

  private InitListener mInitListener = new InitListener() {

    @Override
    public void onInit(int code) {
      Log.d(TAG, "SpeechRecognizer init() code = " + code);
      if (code != ErrorCode.SUCCESS) {
        showTip("初始化失败，错误码：" + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
      }
    }
  };

  public XFYunLATModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "XFYunLATModule";
  }


  private void Init(){
    if(mIat != null) return;

    SpeechUtility.createUtility(this.getReactApplicationContext(), SpeechConstant.APPID +"=d9085fb6");
    mIat = SpeechRecognizer.createRecognizer(this.getCurrentActivity(), mInitListener);
  }

  @ReactMethod
  public void startRecord(final Promise promise) {
    this.Init();
    this.executeStream();
    promise.resolve(ret);
  }

  @ReactMethod
  public void stopRecord(final Promise promise) {
    mIat.stopListening();
    promise.resolve(true);
  }

  /**
   * 听写监听器。
   */
  private RecognizerListener mRecognizerListener = new RecognizerListener() {

    @Override
    public void onBeginOfSpeech() {
      // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
      showTip("开始说话");
    }

    @Override
    public void onError(SpeechError error) {
      // Tips：
      // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。

      showTip(error.getPlainDescription(true));

    }

    @Override
    public void onEndOfSpeech() {
      // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
      showTip("结束说话");
    }

    @Override
    public void onResult(RecognizerResult results, boolean isLast) {
      Log.d(TAG, results.getResultString());
      if (resultType.equals("json")) {

        printResult(results);

      }else if(resultType.equals("plain")) {
        buffer.append(results.getResultString());
        showTip(buffer.toString());
      }
    }

    @Override
    public void onVolumeChanged(int volume, byte[] data) {
      showTip("当前正在说话，音量大小：" + volume);
      Log.d(TAG, "返回音频数据："+data.length);
    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
      // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
      // 若使用本地能力，会话id为null
      //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
      //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
      //		Log.d(TAG, "session id =" + sid);
      //	}
    }
  };

  //执行音频流识别操作
  private void executeStream() {
    buffer.setLength(0);
    mIatResults.clear();
    // 设置参数
    setParam(null);
    // 设置音频来源为外部文件
//    mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
    // 也可以像以下这样直接设置音频文件路径识别（要求设置文件在sdcard上的全路径）：
    // mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
    //mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, "sdcard/XXX/XXX.pcm");
    ret = mIat.startListening(mRecognizerListener);
    if (ret != ErrorCode.SUCCESS) {
      showTip("识别失败,错误码：" + ret+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
    }
  }

  private void setParam(final ReadableMap options) {

    //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
        mIat.setParameter( SpeechConstant.SUBJECT, null );
    //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
    //此处engineType为“cloud”
        mIat.setParameter( SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD );
    //设置语音输入语言，zh_cn为简体中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
    //设置结果返回语言
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
    // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
    //取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
    //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
    //自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
    //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,"1");
  }


  private void showTip(final String str) {
    Log.d(TAG, str);
  }

  private void printResult(RecognizerResult results) {
    String text = JsonParser.parseIatResult(results.getResultString());

    String sn = null;
    // 读取json结果中的sn字段
    try {
      JSONObject resultJson = new JSONObject(results.getResultString());
      sn = resultJson.optString("sn");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    mIatResults.put(sn, text);

    StringBuffer resultBuffer = new StringBuffer();
    for (String key : mIatResults.keySet()) {
      resultBuffer.append(mIatResults.get(key));
    }

    showTip(resultBuffer.toString());
  }

}