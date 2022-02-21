package com.libs.palmmob.googlepay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GooglePay implements BillingClientStateListener{

    private static final Handler handler = new Handler(Looper.getMainLooper());

    public interface InitListener {
        void onInitResult(boolean ok);
    }

    public interface ResultListener {
        void onSkuResult(int ret, @NonNull List<SkuDetails> skuDetailsList);
    }

    public interface ConfirmOrderListener {
        void onResult(int ret);
    }

    public interface PurchasesListener {
        void onResult(int ret, List<Purchase> purchases);
    }

    private BillingClient billingClient;
    private Dictionary<String, SkuDetails> skuDict = new Hashtable<>();

    private InitListener initListener = null;
    private PurchasesUpdatedListener purchasesUpdatedListener;
    private PurchasesListener buySkuListener = null;
    private Context context;

    public void init(Context context, InitListener listener) {
        this.context = context;

        if(billingClient != null) return;

        purchasesUpdatedListener = (billingResult, purchases) -> {
            if(buySkuListener == null) return;
            buySkuListener.onResult(billingResult.getResponseCode(), null);
            buySkuListener = null;
        };

        connect(listener);

//        billingClient = BillingClient.newBuilder(context)
//                .setListener(purchasesUpdatedListener)
//                .enablePendingPurchases()
//                .build();
//        listener.onInitResult(true);
    }

    public void connect(InitListener listener){
        this.initListener = listener;

        if(billingClient != null) {
            if(billingClient.isReady()){
                billingClient.endConnection();
            }
            billingClient = null;
        }

        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(this);
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        Log.e("GooglePay", "code=" + billingResult.getResponseCode() + " ; " +  billingResult.getDebugMessage());

        if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
            if(this.initListener != null){
                this.initListener.onInitResult(true);
                this.initListener = null;
            }
        } else {
            if(this.initListener != null){
                this.initListener.onInitResult(false);
                this.initListener = null;
            }
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        Log.e("GooglePay","onBillingServiceDisconnected");
        retryBillingServiceConnectionWithExponentialBackoff();
    }

    private void retryBillingServiceConnectionWithExponentialBackoff() {
        handler.postDelayed(() ->
            connect(null),
        500);
    }

    public void disconnect(){
        if(billingClient.isReady()){
            billingClient.endConnection();
        }
    }

    public boolean supportFeature(String type){
        return billingClient.isFeatureSupported(type).getResponseCode() == 0;
    }

    public void queySku(List<String> skuList, String skutype, ResultListener listener){
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(skutype);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if(skuDetailsList != null && skuDetailsList.size() > 0) {
                        for(SkuDetails skudetail : skuDetailsList){
                            skuDict.put(skudetail.getSku(), skudetail);
                        }
                    }
                    listener.onSkuResult(billingResult.getResponseCode(), skuDetailsList);
                });
    }

    public void queySku(String sku, String skutype, ResultListener listener){
        SkuDetails skudetail = skuDict.get(sku);
        if(skudetail != null) {
            ArrayList<SkuDetails> arr = new ArrayList<>();
            arr.add(skudetail);
            listener.onSkuResult(0, arr);
            return;
        }

        ArrayList<String> skuid_list = new ArrayList<>();
        skuid_list.add(sku);
        this.queySku(skuid_list, skutype, listener);
    }

    public void buySku(Activity activity, SkuDetails skuDetails, PurchasesListener listener) {
        buySkuListener = listener;

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();


        int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
        if(responseCode != BillingClient.BillingResponseCode.OK){
            listener.onResult(responseCode, null);  //出错直接返回错误，否则从购买回调中返回
            buySkuListener = null;
        }
    }


    //发现未确认的订单，确认完再返回
    public void queryPurchases(String skutype, PurchasesListener listener){
        billingClient.queryPurchasesAsync(skutype, (billingResult, purchases) ->  {
            int code = billingResult.getResponseCode();
            if(code != BillingClient.BillingResponseCode.OK) {
                listener.onResult(code, null);
                return;
            }

            List<Purchase> need_list = getNeedConfirmList(purchases);
            int acknowledge_count = 0;
            if(need_list != null){
                acknowledge_count = need_list.size();
            }

            if(acknowledge_count == 0){                                 //全部都是已处理的订单
                listener.onResult(0, purchases);
                return;
            }

            AtomicInteger processed_count = new AtomicInteger();
            for(Purchase purchase : need_list){
                int finalAcknowledge_count = acknowledge_count;

                if(skutype.equals(BillingClient.SkuType.SUBS)){
                    this.acknowledgePurchase(purchase, ret -> {
                        processed_count.getAndIncrement();
                        if(processed_count.get() == finalAcknowledge_count){
                            listener.onResult(0, purchases);
                        }
                    });
                } else if(skutype.equals(BillingClient.SkuType.INAPP)){
                    this.consumePurchase(purchase, ret -> {
                        processed_count.getAndIncrement();
                        if(processed_count.get() == finalAcknowledge_count){
                            listener.onResult(0, purchases);
                        }
                    });
                }
            }
        });
    }


    private void acknowledgePurchase(Purchase purchase, ConfirmOrderListener listener){
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(params, billingResult -> {
            Log.e("GooglePay", billingResult.getDebugMessage());
            listener.onResult(billingResult.getResponseCode());
        });
    }

    private void consumePurchase(Purchase purchase, ConfirmOrderListener listener){
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        billingClient.consumeAsync(consumeParams, (billingResult, s) -> {
            Log.e("GooglePay", billingResult.getDebugMessage() + ";" + s);
            listener.onResult(billingResult.getResponseCode());
        });
    }

    private List<Purchase> getNeedConfirmList(List<Purchase> purchases){
        if(purchases == null || purchases.size() == 0){
            return null;
        }
        List<Purchase> need_list = new ArrayList<>();
        for(Purchase purchase : purchases) {
            if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
                continue;
            }
            if (purchase.isAcknowledged()) {
                continue;
            }
            need_list.add(purchase);
        }
        return need_list;
    }
}
