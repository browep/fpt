package com.github.browep.fpt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import com.github.browep.fpt.R;
import com.github.browep.fpt.dao.DaoAwareActivity;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 12:55 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SubmittableActivity extends DaoAwareActivity {
  protected Activity self = this;

  protected View.OnKeyListener hideKeyBoardListener = new View.OnKeyListener() {
    /**
     * This listens for the user to press the enter button on
     * the keyboard and then hides the virtual keyboard
     */
    public boolean onKey(View arg0, int arg1, KeyEvent event) {
      // If the event is a key-down event on the "enter" button
      if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
          (arg1 == KeyEvent.KEYCODE_ENTER)) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
        return true;
      }
      return false;
    }
  };


  //    SubmittableActivity self = this;
    public abstract void onSubmit(View view);

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onSubmit(view);
            }
        });

        final View commentBox = findViewById(R.id.comment);

        if (commentBox != null) {
            // kill keyboard when enter is pressed
          commentBox.setOnKeyListener(hideKeyBoardListener);
        }
    }

    protected void updateSubmitButtonText(String buttonText){
        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setText(buttonText);

    }
}


