package com.github.browep.fpt.dao;

import android.app.Activity;
import android.os.Bundle;
import com.flurry.android.FlurryAgent;
import com.github.browep.fpt.C;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalyzableActivity extends Activity {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, C.FLURRY_ID);
    }

    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
        // your code
    }
}
