package com.libs.palmmob.oaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import java.lang.reflect.Method;
import java.util.HashSet;

public class DeviceIdUtils {

    public static String getDeviceId(Context context, int slotId) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = telephonyManager.getClass().getMethod("getDeviceId", int.class);
            return (String) method.invoke(telephonyManager, slotId);
//            return telephonyManager.getDeviceId(slotId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getImei(Context context, int slotId) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = telephonyManager.getClass().getMethod("getImei", int.class);
            return (String) method.invoke(telephonyManager, slotId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getImei(Context context) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = telephonyManager.getClass().getMethod("getImei");
            return (String) method.invoke(telephonyManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static HashSet<String> getDeviceIds(Context context) {
        try {
            String imei1 = getDeviceId(context, 0);
            String imei2 = getDeviceId(context, 1);
            String imei3 = getDeviceId(context);
            String imei4 = getImei(context, 0);
            String imei5 = getImei(context, 1);
            String imei6 = getImei(context);

            HashSet<String> hashSet = new HashSet();
            if (!TextUtils.isEmpty(imei1)) {
                hashSet.add(imei1);
            }
            if (!TextUtils.isEmpty(imei2)) {
                hashSet.add(imei2);
            }
            if (!TextUtils.isEmpty(imei3)) {
                hashSet.add(imei3);
            }
            if (!TextUtils.isEmpty(imei4)) {
                hashSet.add(imei4);
            }
            if (!TextUtils.isEmpty(imei5)) {
                hashSet.add(imei5);
            }
            if (!TextUtils.isEmpty(imei6)) {
                hashSet.add(imei6);
            }


            return hashSet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAndroidId(Context context) {
        try {
            return Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
