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

    Button createWorkoutButton = (Button) findViewById(R.id.create_workout_button);
    createWorkoutButton.setOnClickListener(createWorkoutButtonOnClickListener);

    Button enterDataButton = (Button) findViewById(R.id.enter_workout_entry_button);
    enterDataButton.setOnClickListener(enterDataOnClickListener);

    Button seeProgressButton = (Button) findViewById(R.id.see_progress);
    seeProgressButton.setOnClickListener(seeProgressOnClickListener);

    Button takeProgressPicture = (Button) findViewById(R.id.progress_picture);
    takeProgressPicture.setOnClickListener(takeProgressPictureOnClickListener);

    Button sendReportButton = (Button) findViewById(R.id.send_report);
    sendReportButton.setOnClickListener(sendReportOnClickListenere);

  }


  View.OnClickListener createWorkoutButtonOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {
      Intent intent = new Intent();
      intent.setClass(self, CreateWorkout.class);
      self.startActivity(intent);
    }
  };

  View.OnClickListener enterDataOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {
      Intent intent = new Intent();
      intent.setClass(self, EnterDataChooser.class);
      self.startActivity(intent);
    }
  };

  View.OnClickListener seeProgressOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {
      Intent intent = new Intent();
      intent.setClass(self, SeeProgressChooser.class);
      self.startActivity(intent);
    }
  };

  View.OnClickListener takeProgressPictureOnClickListener = new View.OnClickListener() {
    public void onClick(View view) {

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
  };

  View.OnClickListener sendReportOnClickListenere = new View.OnClickListener() {
    public void onClick(View view) {
      if(getFptApplication().getPreferencesService().getBooleanPreference(C.AUTHORIZED_FOR_REPORT)){
//      if(false){
        startActivity(new Intent(self, SendReport.class));
      } else {
        self.startActivity(new Intent(self, ReportPaymentChooser.class));
      }
    }
  };


  @Override
  public String getPageName() {
    return "Welcome";
  }
}

