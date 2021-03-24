package com.libs.palmmob;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.WindowManager;

import java.lang.ref.WeakReference;


public class SplashScreen {
  private static Dialog mSplashDialog;
  private static WeakReference<Activity> mActivity;

  /**
   * 打开启动屏
   */
  public static void show(final Activity activity) {
    if (activity == null) return;
    mActivity = new WeakReference<Activity>(activity);
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        if (!activity.isFinishing()) {
          mSplashDialog = new Dialog(activity, R.style.SplashScreen_Fullscreen);
          mSplashDialog.setContentView(R.layout.activity_ddstarlaunch);
          mSplashDialog.setCancelable(false);
          if (!mSplashDialog.isShowing()) {
            mSplashDialog.show();
          }
        }
      }
    });
  }

  /**
   * 关闭启动屏
   */
  public static void hide(Activity activity) {
    if (activity == null) {
      if (mActivity == null) {
        return;
      }
      activity = mActivity.get();
    }

    if (activity == null) return;

    final Activity _activity = activity;

    _activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        _activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        if (mSplashDialog != null && mSplashDialog.isShowing()) {
          boolean isDestroyed = false;

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            isDestroyed = _activity.isDestroyed();
          }

          if (!_activity.isFinishing() && !isDestroyed) {
            mSplashDialog.dismiss();
          }
          mSplashDialog = null;
        }
      }
    });
  }

}
