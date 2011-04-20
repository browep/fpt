package com.github.browep.fpt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import com.github.browep.fpt.R;
import com.github.browep.fpt.UploadImageTask;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.dao.FptSqliteOpener;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Welcome extends DaoAwareActivity {
  private Welcome self = this;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

//    getDao().dumpDbToLog();

    setContentView(R.layout.main);

    Button createWorkoutButton = (Button) findViewById(R.id.create_workout_button);
    createWorkoutButton.setOnClickListener(createWorkoutButtonOnClickListener);

    Button enterDataButton = (Button) findViewById(R.id.enter_workout_entry_button);
    enterDataButton.setOnClickListener(enterDataOnClickListener);

    Button seeProgressButton = (Button) findViewById(R.id.see_progress);
    seeProgressButton.setOnClickListener(seeProgressOnClickListener);

    Button takeProgressPicture = (Button) findViewById(R.id.progress_picture);
    takeProgressPicture.setOnClickListener(takeProgressPictureOnClickListener);

  }

  private void dumpDataAsJson() {
    File publicFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


    File dbFile = new File( "/data/data/"+ getApplication().getPackageName() + "/databases/" + FptSqliteOpener.getDbName());

    Log.i("dbFile size: " + dbFile.length());

    Util.copyfile(dbFile.getAbsolutePath(),publicFilesDir.getAbsolutePath() + "/main_db");

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      StringBuilder sb = new StringBuilder("{\"models\":");
      (new ObjectMapper()).writeValue(baos, getViewService().getModels());
      sb.append(baos.toString()).append(",").append(getDao().dataToJson()).append("}");
      Log.i(sb.toString());

    } catch (IOException e) {
      Log.e("", e);
    }


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
      Intent intent = new Intent();
      intent.setClass(self, TakeProgressPicture.class);
      self.startActivity(intent);
    }
  };


  @Override
  public String getPageName() {
    return "Welcome";
  }
}

