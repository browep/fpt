package com.github.browep.fpt.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;
import com.github.browep.fpt.R;

public class ReportPaymentChooser extends FptActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    setContentView(R.layout.report_payment_chooser);
    TextView blurbView = (TextView) findViewById(R.id.report_payment_chooser_text_blurb);
    String blurbText =  getResources().getText(R.string.report_payment_chooser_top_text).toString();
    blurbView.setText(Html.fromHtml( blurbText));
    
  }
}
