package com.github.browep.fpt.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.connector.Tweeter;
import com.github.browep.fpt.util.Log;
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


  private Twitter.DialogListener dialogListener = new Twitter.DialogListener() {
    public void onComplete(Bundle values) {

      getFptApplication().getPreferencesService().setStringPreference(C.TWITTER_SECRET_TOKEN, values.getString("secret_token"));
      getFptApplication().getPreferencesService().setStringPreference(C.TWITTER_ACCESS_TOKEN, values.getString("access_token"));

    }

    public void onTwitterError(TwitterError e) {

      Log.e("onTwitterError called for TwitterDialog",new Exception(e));
    }

    public void onError(DialogError e) {
      Log.e("onError called for TwitterDialog",new Exception(e));

    }

    public void onCancel() {
      Log.e("onCancel");
    }
  };

  public static final String TWITTER_OAUTH_REQUEST_TOKEN_ENDPOINT = "http://twitter.com/oauth/request_token";
  public static final String TWITTER_OAUTH_ACCESS_TOKEN_ENDPOINT = "http://twitter.com/oauth/access_token";
  public static final String TWITTER_OAUTH_AUTHORIZE_ENDPOINT = "http://twitter.com/oauth/authorize";
  private CommonsHttpOAuthProvider commonsHttpOAuthProvider = new CommonsHttpOAuthProvider(TWITTER_OAUTH_REQUEST_TOKEN_ENDPOINT,TWITTER_OAUTH_ACCESS_TOKEN_ENDPOINT,TWITTER_OAUTH_AUTHORIZE_ENDPOINT);
  private CommonsHttpOAuthConsumer commonsHttpOAuthConsumer = new CommonsHttpOAuthConsumer(C.TWITTER_CONSUMER_KEY, C.TWITTER_CONSUMER_SECRET);
  private TwDialog dialog;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      tweetText = Util.slurp(getFptApplication().getApplicationContext().getResources().openRawResource(R.raw.promo_tweet));
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

  }


  View.OnClickListener tweetOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {

      FlurryAgent.onEvent("TWEET_CLICKED");
      getFptApplication().getTracker().trackEvent("Report","Tweet Clicked",null,0);

      commonsHttpOAuthProvider.setOAuth10a(true);
      dialog = new TwDialog(self,commonsHttpOAuthProvider,commonsHttpOAuthConsumer,dialogListener, R.drawable.swt_72_72);
      dialog.show();

      dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
        public void onDismiss(DialogInterface dialogInterface) {
          AlertDialog.Builder builder = new AlertDialog.Builder(self);
          builder.setCancelable(true);
          builder.setTitle("Tweet about Simple Workout Tracker");
          builder.setInverseBackgroundForced(true);
          builder.setMessage("Simple Workout Tracker will now change your status to \"" + tweetText + "\"");

          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              FlurryAgent.onEvent("TWEET_OK");
              getFptApplication().getTracker().trackEvent("Report","Tweet OK",null,0);


              Tweeter tweeter = new Tweeter(getFptApplication().getPreferencesService().getStringPreference(C.TWITTER_ACCESS_TOKEN),
                  getFptApplication().getPreferencesService().getStringPreference(C.TWITTER_SECRET_TOKEN));
              tweeter.tweet(tweetText);
              getFptApplication().getPreferencesService().setBooleanPreference(C.AUTHORIZED_FOR_REPORT,true);
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
    finish();
  }
}
