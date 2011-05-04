/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.browep.fpt.billing;

import com.github.browep.fpt.R;
import com.github.browep.fpt.billing.BillingService.RequestPurchase;
import com.github.browep.fpt.billing.BillingService.RestoreTransactions;
import com.github.browep.fpt.billing.Consts.PurchaseState;
import com.github.browep.fpt.billing.Consts.ResponseCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * A sample application that demonstrates in-app billing.
 */
public class Dungeons extends Activity
         {
    private static final String TAG = "Dungeons";

    /**
     * Used for storing the log text.
     */

    /**
     * The SharedPreferences key for recording whether we initialized the
     * database.  If false, then we perform a RestoreTransactions request
     * to get all the purchases for this user.
     */

    private FptPurchaseObserver mFptPurchaseObserver;
    private Handler mHandler;

    private BillingService mBillingService;



    private static final int DIALOG_CANNOT_CONNECT_ID = 1;
    private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);

        mHandler = new Handler();
        mFptPurchaseObserver = new FptPurchaseObserver(this,mHandler);
        mBillingService = new BillingService();
        mBillingService.setContext(this);

        // Check if billing is supported.
        ResponseHandler.register(mFptPurchaseObserver);
        if (!mBillingService.checkBillingSupported()) {
            showDialog(DIALOG_CANNOT_CONNECT_ID);
        }
    }

    /**
     * Called when this activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ResponseHandler.register(mFptPurchaseObserver);
    }

    /**
     * Called when this activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
        ResponseHandler.unregister(mFptPurchaseObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBillingService.unbind();
    }

    /**
     * Save the context of the log so simple things like rotation will not
     * result in the log being cleared.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Restore the contents of the log if it has previously been saved.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_CANNOT_CONNECT_ID:
            return createDialog(R.string.cannot_connect_title,
                    R.string.cannot_connect_message);
        case DIALOG_BILLING_NOT_SUPPORTED_ID:
            return createDialog(R.string.billing_not_supported_title,
                    R.string.billing_not_supported_message);
        default:
            return null;
        }
    }

    private Dialog createDialog(int titleId, int messageId) {
        String helpUrl = replaceLanguageAndRegion(getString(R.string.help_url));
        if (Consts.DEBUG) {
            Log.i(TAG, helpUrl);
        }
        final Uri helpUri = Uri.parse(helpUrl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId)
            .setIcon(android.R.drawable.stat_sys_warning)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(R.string.learn_more, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, helpUri);
                    startActivity(intent);
                }
            });
        return builder.create();
    }

    /**
     * Replaces the language and/or country of the device into the given string.
     * The pattern "%lang%" will be replaced by the device's language code and
     * the pattern "%region%" will be replaced with the device's country code.
     *
     * @param str the string to replace the language/country within
     * @return a string containing the local language and region codes
     */
    private String replaceLanguageAndRegion(String str) {
        // Substitute language and or region if present in string
        if (str.contains("%lang%") || str.contains("%region%")) {
            Locale locale = Locale.getDefault();
            str = str.replace("%lang%", locale.getLanguage().toLowerCase());
            str = str.replace("%region%", locale.getCountry().toLowerCase());
        }
        return str;
    }



 /**
     * Called when a button is pressed.
  */
 public void onClick(View v) {



 }

}
