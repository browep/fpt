package com.github.browep.fpt.ui;

import android.os.Bundle;
import android.widget.EditText;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.util.StringUtils;

public class SendReport extends FptActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.send_report);

    String reportEmail = getFptApplication().getPreferencesService().getStringPreference(C.REPORT_EMAIL);
    if (!StringUtils.isEmpty(reportEmail)) {
      ((EditText) findViewById(R.id.send_report_email)).setText(reportEmail);
    }
  }
}
