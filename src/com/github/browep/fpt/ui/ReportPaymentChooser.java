package com.github.browep.fpt.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.flurry.android.FlurryAgent;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.billing.BillingService;
import com.github.browep.fpt.billing.FptPurchaseObserver;
import com.github.browep.fpt.billing.ResponseHandler;
import com.github.browep.fpt.connector.Tweeter;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;
import com.sugree.twitter.DialogError;
import com.sugree.twitter.TwDialog;
import com.sugree.twitter.Twitter;
import com.sugree.twitter.TwitterError;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportPaymentChooser extends FptActivity {

  private String tweetText;

  private static final int DIALOG_CANNOT_CONNECT_ID = 1;
  private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;

  Facebook facebook = new Facebook(C.FACEBOOK_APP_ID);

  public static final String TWITTER_OAUTH_REQUEST_TOKEN_ENDPOINT = "http://twitter.com/oauth/request_token";
  public static final String TWITTER_OAUTH_ACCESS_TOKEN_ENDPOINT = "http://twitter.com/oauth/access_token";
  public static final String TWITTER_OAUTH_AUTHORIZE_ENDPOINT = "http://twitter.com/oauth/authorize";
  private CommonsHttpOAuthProvider commonsHttpOAuthProvider = new CommonsHttpOAuthProvider(TWITTER_OAUTH_REQUEST_TOKEN_ENDPOINT, TWITTER_OAUTH_ACCESS_TOKEN_ENDPOINT, TWITTER_OAUTH_AUTHORIZE_ENDPOINT);
  private CommonsHttpOAuthConsumer commonsHttpOAuthConsumer = new CommonsHttpOAuthConsumer(C.TWITTER_CONSUMER_KEY, C.TWITTER_CONSUMER_SECRET);
  private TwDialog dialog;
  private Handler mHandler;
  private ProgressDialog mPurchaseProgressDialog;
  private FptPurchaseObserver mFptPurchaseObserver;
  private BillingService mBillingService;
  private String facebookText;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      String promotionalText = Util.slurp(getFptApplication().getApplicationContext().getResources().openRawResource(R.raw.promo_tweet));
      tweetText = promotionalText.trim() + " " + C.TWITTER_MARKET_LINK;
      facebookText = promotionalText;
    } catch (IOException e) {
      Log.e("issue trying to get the promo_tweet.txt", e);
    }

    InputStream inputStream =
        getFptApplication().getApplicationContext().getResources().openRawResource(R.raw.report_payment_chooser_blurb);


    setContentView(R.layout.report_payment_chooser);
    TextView blurbView = (TextView) findViewById(R.id.report_payment_chooser_text_blurb);
    try {
      blurbView.setText(Html.fromHtml(Util.slurp(inputStream)));
    } catch (IOException e) {
      Log.e("issue slurping report payment chooser blurb", e);
    }

    findViewById(R.id.tweet_button).setOnClickListener(tweetOnClickListener);
    findViewById(R.id.facebook_button).setOnClickListener(facebookOnClickListener);
    findViewById(R.id.buy_button).setOnClickListener(buyButtonOnClickListener);


    mHandler = new ReportPaymentChooserHandler();
    mPurchaseProgressDialog = new ProgressDialog(self);
    mFptPurchaseObserver = new FptPurchaseObserver(this, mHandler,mPurchaseProgressDialog);
    mBillingService = new BillingService();
    mBillingService.setContext(this);

    // Check if billing is supported.
    ResponseHandler.register(mFptPurchaseObserver);
    if (!mBillingService.checkBillingSupported()) {
      Log.i("billing NOT supported");
    } else {
      Log.i("billing IS supported");
    }
  }

  View.OnClickListener facebookOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {
      FlurryAgent.onEvent("FB_CLICKED");
      getFptApplication().getTracker().trackEvent("Report", "Facebook Clicked", null, 0);
      doFacebookPost();
    }
  };

  View.OnClickListener tweetOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {

      FlurryAgent.onEvent("TWEET_CLICKED");
      getFptApplication().getTracker().trackEvent("Report", "Tweet Clicked", null, 0);

      commonsHttpOAuthProvider.setOAuth10a(true);
      dialog = new TwDialog(self, commonsHttpOAuthProvider, commonsHttpOAuthConsumer, dialogListener, R.drawable.swt_72_72);
      dialog.show();

      dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
        public void onDismiss(DialogInterface dialogInterface) {
          AlertDialog.Builder builder = new AlertDialog.Builder(self);
          builder.setCancelable(true);
          builder.setTitle("Tweet about Simple Workout Tracker");
          builder.setInverseBackgroundForced(true);
          builder.setMessage("Simple Workout Tracker will now change your status to \"" + tweetText + "\"");

          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              FlurryAgent.onEvent("TWEET_OK");
              getFptApplication().getTracker().trackEvent("Report", "Tweet OK", null, 0);


              Tweeter tweeter = new Tweeter(getFptApplication().getPreferencesService().getStringPreference(C.TWITTER_ACCESS_TOKEN),
                  getFptApplication().getPreferencesService().getStringPreference(C.TWITTER_SECRET_TOKEN));
              tweeter.tweet(tweetText);
              getFptApplication().getPreferencesService().setBooleanPreference(C.AUTHORIZED_FOR_REPORT, true);
              startActivityForResult(new Intent(self, SendReport.class), 0);

            }
          });

          builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
            }
          });
          AlertDialog alert = builder.create();
          alert.show();
        }
      });

    }
  };

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == R.id.facebook_result_id) {
      if (resultCode == R.id.facebook_result_success) {
        // popup dialog for posting to wall
        doFacebookPost();

      } else if (resultCode == R.id.facebook_result_failure) {
        Util.longToastMessage(self, "Facebook Authorization failed.  Please try again or try another option");
      }

    } else {
      // this activity is done, go back to home screen
      finish();
    }
  }

  private void doFacebookPost() {
    Bundle parameters = new Bundle();

    Map attachment = new HashMap();
    attachment.put("description", facebookText);
    attachment.put("href", C.FACEBOOK_MARKET_LINK);
    attachment.put("name", "Checkout Simple Workout Tracker for Android");

    parameters.putString("attachment", Util.toJson(attachment));// the message to post to the wall
    facebook.dialog(self, "stream.publish", parameters, new Facebook.FacebookDialogListener() {
      public void onComplete(Bundle values) {

        if (!StringUtils.isEmpty(values.getString("post_id"))) {
          getFptApplication().getPreferencesService().setBooleanPreference(C.AUTHORIZED_FOR_REPORT, true);
          FlurryAgent.onEvent("FACEBOOK_POSTED");
          getFptApplication().getTracker().trackEvent("Report", "Facebook Posted", null, 0);
          startActivity(new Intent(self, SendReport.class));
        } else {
          FlurryAgent.onEvent("FACEBOOK_CANCELED");
          getFptApplication().getTracker().trackEvent("Report", "Facebook Canceled", null, 0);
          onFailure();
        }
      }

      private void onFailure() {
        Util.longToastMessage(self, "Message not sent, sorry.  Either try again or try another option.");
      }

      public void onFacebookError(FacebookError e) {
        Log.e("onFacebookError", new Exception(e));
        onFailure();
      }

      public void onError(com.facebook.android.DialogError e) {
        Log.e("onError", new Exception(e));
        onFailure();
      }

      public void onCancel() {
        Log.i("onCancel");
        onFailure();
      }
    });
  }


  private Twitter.DialogListener dialogListener = new Twitter.DialogListener() {
    public void onComplete(Bundle values) {

      getFptApplication().getPreferencesService().setStringPreference(C.TWITTER_SECRET_TOKEN, values.getString("secret_token"));
      getFptApplication().getPreferencesService().setStringPreference(C.TWITTER_ACCESS_TOKEN, values.getString("access_token"));

    }

    public void onTwitterError(TwitterError e) {

      Log.e("onTwitterError called for TwitterDialog", new Exception(e));
    }

    public void onError(DialogError e) {
      Log.e("onError called for TwitterDialog", new Exception(e));

    }

    public void onCancel() {
      Log.e("onCancel");
    }
  };

  View.OnClickListener buyButtonOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {
      FlurryAgent.onEvent("BUY_CLICKED");
      getFptApplication().getTracker().trackEvent("Report", "Buy Clicked", null, 0);
      if (!mBillingService.requestPurchase("send_report_01", null)) {
        showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
      }
    }
  };

  /**
   * Called when this activity becomes visible.
   */
  @Override
  public void onStart() {
    super.onStart();
    ResponseHandler.register(mFptPurchaseObserver);
  }

  /**
   * Called when this activity is no longer visible.
   */
  @Override
  public void onStop() {
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

  class ReportPaymentChooserHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      if(msg.what == R.id.purchase_successful){
        try {
          mPurchaseProgressDialog.dismiss();
          self.startActivity(new Intent(self,SendReport.class));
        } catch (Exception e) {
          Log.e("error trying to dismiss progress dialog", e);
        }
      }else{
        Log.wtf("unkown msg sent: " + msg.what);
      }
    }
  }

}
