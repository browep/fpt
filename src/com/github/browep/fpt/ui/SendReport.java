package com.github.browep.fpt.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.flurry.android.FlurryAgent;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendReport extends FptActivity {
  public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",Pattern.CASE_INSENSITIVE);
  private EditText emailBox;
  private Handler mHandler;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.send_report);

    mHandler = new Handler();

    String reportEmail = getFptApplication().getPreferencesService().getStringPreference(C.REPORT_EMAIL);
    emailBox = ((EditText) findViewById(R.id.send_report_email));
    if (!StringUtils.isEmpty(reportEmail)) {
      emailBox.setText(reportEmail);
    }

    ((Button) findViewById(R.id.send_button)).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        final String email = emailBox.getText().toString();
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
          final Map params= new HashMap();
          params.put("email",email);
          FlurryAgent.onEvent("SENDING_REPORT",params);

          final ProgressDialog mSpinner = new ProgressDialog(self);
          mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
          mSpinner.setMessage("Sending...");
          mSpinner.show();

          new Thread() {
            @Override
            public void run() {
              try {
                getFptApplication().sendReport(email);
                // save this email as a good email
                getFptApplication().getPreferencesService().setStringPreference(C.REPORT_EMAIL, email);
                FlurryAgent.onEvent("REPORT_SENT", params);
                getFptApplication().getTracker().trackEvent("Report", "Sent", email, 0);

                mHandler.post(new Runnable() {
                  public void run() {
                    mSpinner.dismiss();
                    Util.longToastMessage(self, "Report has been sent to: " + email);
                    finish();
                  }
                });
              } catch (Exception e) {
                Log.e("error trying to send report to " + email, e);
              }
            }
          }.start();


        }
        else{
          Util.longToastMessage(self,"Email is invalid, please try again");
        }

      }
    });
  }
}
