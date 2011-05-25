package com.github.browep.fpt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.UpdateImagesTask;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.model.FptPicture;
import com.github.browep.nosql.NoSqlSqliteOpener;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Welcome extends DaoAwareActivity {
  private Welcome self = this;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getDao().dumpDbToLog();

    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      new UpdateImagesTask().execute(getFptApplication());
    }

    setContentView(R.layout.main);

  }


  public void createWorkoutButtonOnClickListener(View view) {
    Intent intent = new Intent();
    intent.setClass(self, CreateWorkout.class);
    self.startActivity(intent);
  }

  public void enterDataOnClickListener(View view) {
    Intent intent = new Intent();
    intent.setClass(self, EnterDataChooser.class);
    self.startActivity(intent);
  }

  public void seeProgressOnClickListener(View view) {
    Intent intent = new Intent();
    intent.setClass(self, SeeProgressChooser.class);
    self.startActivity(intent);
  }

  public void takeProgressPictureOnClickListener(View view) {

    // check to see if the SD-card is mounted
    String state = Environment.getExternalStorageState();
    if (!Environment.MEDIA_MOUNTED.equals(state)) {
      Util.longToastMessage(self, C.SD_CARD_NOT_MOUNTED_MESSAGE);
    } else {
      Intent intent = new Intent();
      intent.setClass(self, TakeProgressPicture.class);
      self.startActivity(intent);
    }
  }

  public void sendReportOnClickListener(View view) {
    if (getFptApplication().getPreferencesService().getBooleanPreference(C.AUTHORIZED_FOR_REPORT)) {
//      if(false){
      startActivity(new Intent(self, SendReport.class));
    } else {
      self.startActivity(new Intent(self, ReportPaymentChooser.class));
    }
  }


  @Override
  public String getPageName() {
    return "Welcome";
  }
}

