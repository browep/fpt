package com.github.browep.fpt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

   protected View.OnKeyListener submitKeyboardListener = new View.OnKeyListener() {
    /**
     * This listens for the user to press the enter button on
     * the keyboard and then hides the virtual keyboard
     */
    public boolean onKey(View arg0, int arg1, KeyEvent event) {
      // If the event is a key-down event on the "enter" button
      if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
          (arg1 == KeyEvent.KEYCODE_ENTER)) {
        onSubmit(null);
        return true;
      }
      return false;
    }
  };

  protected TextView.OnEditorActionListener editorSubmitListener = new TextView.OnEditorActionListener() {
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
      Log.i(this.getClass().getName(), "oneditoraction=" + i);
      if (EditorInfo.IME_ACTION_DONE == i || EditorInfo.IME_ACTION_GO == i || EditorInfo.IME_ACTION_NEXT == i) {
        onSubmit(null);
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

    final EditText commentBox = (EditText) findViewById(R.id.comment);

    if (commentBox != null) {
      // kill keyboard when enter is pressed

      commentBox.setOnEditorActionListener(editorSubmitListener);
      commentBox.setOnKeyListener(submitKeyboardListener);
    }
  }

  protected void updateSubmitButtonText(String buttonText) {
    Button doneButton = (Button) findViewById(R.id.done_button);
    doneButton.setText(buttonText);

  }
}


