package com.github.browep.fpt.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/6/11
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    public static Toast longToastMessage(Context context, String message) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
        return toast;
    }


}
