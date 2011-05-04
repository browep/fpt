package com.github.browep.fpt.billing;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import com.flurry.android.FlurryAgent;
import com.github.browep.fpt.FptApp;

public class FptPurchaseObserver extends PurchaseObserver {
  public static final String TAG = "FptPurchaseObserver";

  public FptPurchaseObserver(Activity activity, Handler handler) {
    super(activity, handler);
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

    if (developerPayload == null) {
      logProductActivity(itemId, purchaseState.toString());
    } else {
      logProductActivity(itemId, purchaseState + "\n\t" + developerPayload);
    }


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
