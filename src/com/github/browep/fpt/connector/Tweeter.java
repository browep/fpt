package com.github.browep.fpt.connector;

import android.net.Uri;
import com.github.browep.fpt.C;
import com.github.browep.fpt.ui.ReportPaymentChooser;
import com.github.browep.fpt.util.Log;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


public class Tweeter {
  private String accessToken;
  private String secretToken;
  protected CommonsHttpOAuthProvider oAuthProvider;
  protected CommonsHttpOAuthConsumer oAuthConsumer;

  public Tweeter(String accessToken, String secretToken) {
    this.accessToken = accessToken;
    this.secretToken = secretToken;
    oAuthProvider = new CommonsHttpOAuthProvider(ReportPaymentChooser.TWITTER_OAUTH_REQUEST_TOKEN_ENDPOINT,
        ReportPaymentChooser.TWITTER_OAUTH_AUTHORIZE_ENDPOINT, ReportPaymentChooser.TWITTER_OAUTH_ACCESS_TOKEN_ENDPOINT);
    oAuthConsumer = new CommonsHttpOAuthConsumer(C.TWITTER_CONSUMER_KEY, C.TWITTER_CONSUMER_SECRET);
    oAuthConsumer.setTokenWithSecret(accessToken, secretToken);
  }

  public boolean tweet(String message) {
    if (message == null && message.length() > 140) {
      throw new IllegalArgumentException("message cannot be null and must be less than 140 chars");
    }
    // create a request that requires authentication

    try {
      HttpClient httpClient = new DefaultHttpClient();
      Uri.Builder builder = new Uri.Builder();
      builder.appendPath("statuses").appendPath("update.json")
          .appendQueryParameter("status", message);
      Uri man = builder.build();
      HttpPost post = new HttpPost("http://twitter.com" + man.toString());
      oAuthConsumer.sign(post);
      HttpResponse resp = httpClient.execute(post);
      if (resp.getStatusLine().getStatusCode() == 200) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      Log.e("trying to tweet: " + message, e);
      return false;
    }

  }
}
