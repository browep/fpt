package com.github.browep.fpt.billing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Window;
import com.flurry.android.FlurryAgent;
import com.github.browep.fpt.C;
import com.github.browep.fpt.FptApp;
import com.github.browep.fpt.R;

public class FptPurchaseObserver extends PurchaseObserver {
  public static final String TAG = "FptPurchaseObserver";
  private ProgressDialog mPurchaseProgressDialog;

  public FptPurchaseObserver(Activity activity, Handler handler, ProgressDialog mPurchaseProgressDialog) {
    super(activity, handler);
    this.mPurchaseProgressDialog = mPurchaseProgressDialog;
  }

  @Override
  public void onBillingSupported(boolean supported) {
    if (Consts.DEBUG) {
      Log.i(TAG, "supported: " + supported);
    }

  }

  @Override
  public void onPurchaseStateChange(Consts.PurchaseState purchaseState, String itemId,
                                    int quantity, long purchaseTime, String developerPayload) {
    if (Consts.DEBUG) {
      Log.i(TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
    }
    // if purchase state was purchased, set to authorized
    if(purchaseState == Consts.PurchaseState.PURCHASED)
      ((FptApp)mActivity.getApplication()).getPreferencesService().setBooleanPreference(C.AUTHORIZED_FOR_REPORT,true);


    if (developerPayload == null) {
      logProductActivity(itemId, purchaseState.toString());
    } else {
      logProductActivity(itemId, purchaseState + "\n\t" + developerPayload);
    }

    mHandler.sendEmptyMessage(R.id.purchase_successful);


  }

  private void logProductActivity(String itemId, String s) {
    Log.i(TAG, itemId + ": " + s);
  }

  @Override
  public void onRequestPurchaseResponse(BillingService.RequestPurchase request,
                                        Consts.ResponseCode responseCode) {
    if (Consts.DEBUG) {
      Log.d(TAG, request.mProductId + ": " + responseCode);
    }
    if (responseCode == Consts.ResponseCode.RESULT_OK) {
      if (Consts.DEBUG) {
        Log.i(TAG, "purchase was successfully received from market");
        mPurchaseProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPurchaseProgressDialog.setMessage("Authorizing...");
        mPurchaseProgressDialog.show();

        FlurryAgent.onEvent("BUY_SUCCESS");
        FptApp.getInstance().getTracker().trackEvent("Report", "Buy Success", null, 0);

      }
      logProductActivity(request.mProductId, "sending purchase request");
    } else if (responseCode == Consts.ResponseCode.RESULT_USER_CANCELED) {
      if (Consts.DEBUG) {
        Log.i(TAG, "user canceled purchase");
      }
      logProductActivity(request.mProductId, "dismissed purchase dialog");
    } else {
      if (Consts.DEBUG) {
        Log.i(TAG, "purchase failed");
      }
      logProductActivity(request.mProductId, "request purchase returned " + responseCode);
    }
  }

  @Override
  public void onRestoreTransactionsResponse(BillingService.RestoreTransactions request,
                                            Consts.ResponseCode responseCode) {
    if (responseCode == Consts.ResponseCode.RESULT_OK) {
      if (Consts.DEBUG) {
        Log.d(TAG, "completed RestoreTransactions request");
      }

    } else {
      if (Consts.DEBUG) {
        Log.d(TAG, "RestoreTransactions error: " + responseCode);
      }
    }
  }
}
