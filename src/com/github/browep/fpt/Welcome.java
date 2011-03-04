package com.github.browep.fpt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.dao.Storable;
import com.github.browep.fpt.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Welcome extends DaoAwareActivity
{
    private Welcome self = this;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button createWorkoutButton = (Button) findViewById(R.id.create_workout_button);
        createWorkoutButton.setOnClickListener(createWorkoutButtonOnClickListener);



    }

    View.OnClickListener createWorkoutButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(self,CreateWorkout.class);
            self.startActivity(intent);
        }
    };
}

