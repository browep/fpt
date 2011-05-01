package com.github.browep.fpt.ui;

import android.content.Intent;
import android.os.Bundle;
import com.facebook.android.*;
import com.facebook.android.Facebook.*;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.util.Log;

public class FacebookAuth extends FptActivity {

    Facebook facebook = new Facebook(C.FACEBOOK_APP_ID);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_auth_window);

        facebook.authorize(this, new String[]{"publish_stream"}, 0,new FacebookDialogListener() {
            public void onComplete(Bundle values) {
              Log.i("onComplete called");
              getFptApplication().getPreferencesService().setStringPreference(C.FACEBOOK_AUTHORIZE_TOKEN,values.getString("access_token"));
              setResult(R.id.facebook_result_success);
              finish();
            }

            public void onFacebookError(FacebookError error) {
              Log.e("onFacebookError:",new Exception(error));
              setResult(R.id.facebook_result_failure);
              finish();
            }

            public void onError(DialogError e) {
              Log.e("onError",new Exception(e));
              setResult(R.id.facebook_result_failure);
              finish();
            }

            public void onCancel() {
              Log.i("onCancel called");
              setResult(R.id.facebook_result_failure);
              finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}