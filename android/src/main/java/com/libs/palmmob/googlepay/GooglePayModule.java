package com.libs.palmmob.googlepay;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

import com.palmmob3.globallibs.pay.GooglePay;

public class GooglePayModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;

    GooglePay googlePay;

    @Override
    public String getName() {
        return "GooglePayModule";
    }

    public GooglePayModule(ReactApplicationContext reactContext) {
        super(reactContext);

        GooglePayModule.reactContext = reactContext;
    }

    @ReactMethod
    public void init(Promise promise) {
        if(googlePay == null){
            googlePay = new GooglePay();
            googlePay.init(GooglePayModule.reactContext, (ret) -> promise.resolve(ret));
            return;
        }
        promise.resolve(true);
    }

    @ReactMethod
    public void queySku(ReadableArray skulist, String skutype, Promise promise) {
        ArrayList<String> skuList = new ArrayList<>();
        ArrayList<Object> objlist = skulist.toArrayList();
        for(Object obj : objlist){
            skuList.add(obj.toString());
        }
        googlePay.queySku(skuList, skutype, (code, skuDetailsList) -> {
            if(skuDetailsList == null || skuDetailsList.size() <= 0) {
                promise.resolve(null);
                return;
            }

            WritableArray arr = Arguments.createArray();
            for(SkuDetails skudetail : skuDetailsList){
                arr.pushString(skudetail.getOriginalJson());
            }
            promise.resolve(arr);
        });
    }

    @ReactMethod
    public void buySku(String sku_id, String skutype, Promise promise) {

        googlePay.queySku(sku_id, skutype, (code, skuDetailsList) -> {

            if(skuDetailsList == null || skuDetailsList.size() <= 0) {

                if(code == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED){

                    googlePay.connect(new GooglePay.InitListener() {
                        @Override
                        public void onInitResult(boolean ok) {
                            buySku(sku_id, skutype, promise);
                        }
                    });
                    return;
                }

                WritableMap params = Arguments.createMap();
                params.putInt("buy_code", code);
                promise.resolve(params);
                return;
            }

            googlePay.buySku(reactContext.getCurrentActivity(), skuDetailsList.get(0), (ret, purchases) -> {
                WritableMap params = Arguments.createMap();
                params.putInt("buy_code", ret);
                promise.resolve(params);
            });
        });
    }

    @ReactMethod
    public void queryPurchases(String skutype, Promise promise) {
        googlePay.queryPurchases(skutype, (ret, purchases) -> {
            WritableMap params = Arguments.createMap();
            params.putInt("code", ret);

            if(purchases != null && purchases.size() > 0){
                WritableArray arr = Arguments.createArray();
                for(Purchase purchase : purchases){
                    arr.pushString(purchase.getOriginalJson());
                }
                params.putArray("orderlist", arr);
            }

            promise.resolve(params);
        });

    }

    @ReactMethod
    public void disconnect(Promise promise) {
        googlePay.disconnect();
        promise.resolve(true);
    }

    @ReactMethod
    public void supportFeature(String type, Promise promise) {
        boolean ret = googlePay.supportFeature(BillingClient.FeatureType.SUBSCRIPTIONS);
        promise.resolve(ret);
    }
}
