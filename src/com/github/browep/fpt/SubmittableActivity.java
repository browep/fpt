package com.github.browep.fpt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.github.browep.fpt.dao.DaoAwareActivity;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 12:55 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SubmittableActivity extends DaoAwareActivity{
//    SubmittableActivity self = this;
    public abstract void onSubmit(View view);

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                onSubmit(view);
            }
        });
    }

}
