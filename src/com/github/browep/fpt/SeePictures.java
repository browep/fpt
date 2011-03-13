package com.github.browep.fpt;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;

import java.io.File;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/12/11
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeePictures extends DaoAwareActivity {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_pictures);
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);

        // get all the pictures
        File pictureDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.github.browep.fpt");
        File[] pictures = pictureDirectory.listFiles();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Display display = getWindowManager().getDefaultDisplay();
        int i = 0;
        for (File picture : pictures) {
            Log.i("adding " +  picture.getAbsolutePath());

            // inflate the image view container
            ViewGroup inflatee = (ViewGroup) inflater.inflate(R.layout.image_text_container, viewFlipper, true);

            LinearLayout imageTextContainer = (LinearLayout) inflatee.getChildAt(i);

            // infate the merge view into the imageTextContainer
            inflater.inflate(R.layout.picture_widget, imageTextContainer, true);

            ImageView imageView = (ImageView) imageTextContainer.findViewById(R.id.progress_image_view);

            Bitmap bitmap = Util.decodeFile(picture,display.getWidth(),display.getHeight());
            imageView.setImageBitmap(bitmap);

            String pictureName = picture.getName().replace(".jpg","");
            Date pictureTaken = new Date(Long.parseLong(pictureName));
            Long deltaDays = (( (new Date()).getTime() - pictureTaken.getTime() ) / C.MILLIS_IN_A_DAY);

            TextView textView = (TextView) imageTextContainer.findViewById(R.id.progress_image_title);
            textView.setText(deltaDays.toString() + " days ago");

            i++;

        }

        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                    viewFlipper.showNext();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                    viewFlipper.showPrevious();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
            return true;
        else
            return false;
    }
}
