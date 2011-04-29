package com.github.browep.fpt.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.flurry.android.FlurryAgent;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.util.StringUtils;
import com.github.browep.fpt.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendReport extends FptActivity {
  private EditText emailBox;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.send_report);

    String reportEmail = getFptApplication().getPreferencesService().getStringPreference(C.REPORT_EMAIL);
    emailBox = ((EditText) findViewById(R.id.send_report_email));
    if (!StringUtils.isEmpty(reportEmail)) {
      emailBox.setText(reportEmail);
    }

    ((Button) findViewById(R.id.send_button)).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        String email = emailBox.getText().toString();
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.matches()) {
          Map params= new HashMap();
          params.put("email",email);
          FlurryAgent.onEvent("SENDING_REPORT",params);

          ProgressDialog mSpinner = new ProgressDialog(self);
          mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
          mSpinner.setMessage("Sending...");
          mSpinner.show();

          getFptApplication().sendReport(email);

          // save this email as a good email
          getFptApplication().getPreferencesService().setStringPreference(C.REPORT_EMAIL,email);
          Util.longToastMessage(self, "Report has been sent to: " + email);
          FlurryAgent.onEvent("REPORT_SENT",params);
          getFptApplication().getTracker().trackEvent("Report", "Sent", email, 0);

          mSpinner.hide();
          finish();

        }
        else{
          Util.longToastMessage(self,"Email is invalid, please try again");
        }

      }
    });
  }
}
