package com.github.browep.fpt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/12/11
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeePictures extends DaoAwareActivity implements ViewSwitcher.ViewFactory {

    private static final String MORE_PICTURES_MESSAGE = "Take more pictures!";
    private Bitmap nextBitmap;
    private Bitmap previousBitmap;
    private ImageSwitcher mSwitcher;
    private Button previousButton;
    private Button nextButton;
    private Integer index = 0;
    private List<File> picturesList;
    private Display display;
     private SeePictures self = this;
    private Bitmap currentBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_pictures);

        previousButton = (Button) findViewById(R.id.previous_button);
        nextButton = (Button) findViewById(R.id.next_button);

        previousButton.setOnClickListener(previousButtonOnClickListener);
        nextButton.setOnClickListener(nextButtonOnClickListener);


        mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));

        // get the earliest picture

        File pictureDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.github.browep.fpt/thumbs");
        File[] pictures = pictureDirectory.listFiles();

        if (pictures.length > 0) {
            picturesList = new LinkedList<File>();
            for (File pictureFile : pictures)
                picturesList.add(pictureFile);

            Collections.sort(picturesList, new Comparator<File>() {
                public int compare(File file, File file1) {
                    return file.getAbsoluteFile().compareTo(file1.getAbsoluteFile());
                }
            });

            File pictureFile = picturesList.get(index);
            display = getWindowManager().getDefaultDisplay();
            currentBitmap = Util.decodeFile(pictureFile);
            mSwitcher.setImageDrawable(new BitmapDrawable(currentBitmap));
            updateTextDislay(pictureFile);

        } else {
            Util.longToastMessage(this,"Need to add pictures first. Click \"Add a Progress Picture\" on the welcome screen first");
            finish();
        }

    }


    View.OnClickListener nextButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (picturesList.size() > 1) {

                mSwitcher.setImageDrawable(new BitmapDrawable(nextBitmap));

                if(index == picturesList.size() -1)
                    index = 0;
                else
                    index += 1;

                File file = picturesList.get(index);
                Log.i("setting to file " + file.getName());

                updateTextDislay(file);

                setupNextImages(index);

            }else{
                Util.longToastMessage(self, MORE_PICTURES_MESSAGE);
            }

        }
    };


    View.OnClickListener previousButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (picturesList.size() > 1) {


                mSwitcher.setImageDrawable(new BitmapDrawable(previousBitmap));
                if(index == 0)
                    index = picturesList.size() -1;
                else
                    index -= 1;

                File file = picturesList.get(index);
                Log.i("setting to file " + file.getName());

                updateTextDislay(file);

                setupNextImages(index);


            }else{
                Util.longToastMessage(self, MORE_PICTURES_MESSAGE);
            }
        }
    };



    private void updateTextDislay(File file) {
        try{
            String name = file.getName().replace(".jpg","");
            Date date = new Date(Long.parseLong(name));
            int dateDelta = (int) (((new Date()).getTime() - date.getTime()) / C.MILLIS_IN_A_DAY);
            TextView pictureTitle = (TextView) findViewById(R.id.picture_title);
            String title = dateDelta == 0 ? "Today" : dateDelta + " day"+(dateDelta == 1? "":"s")+" ago";
            pictureTitle.setText(title);
        }   catch (Exception e){
            Log.e("error with " + file != null ? file.getAbsolutePath() : "null file", e);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupNextImages(0);
    }

    private void setupNextImages(Integer currentIndex) {
        if (picturesList.size() > 1) {

            Integer nextIndex = currentIndex == picturesList.size()-1 ? 0 : currentIndex+1;
            Integer previousIndex = currentIndex == 0 ? picturesList.size()-1 : currentIndex-1;

            nextBitmap = Util.decodeFile(picturesList.get(nextIndex));
            previousBitmap = Util.decodeFile(picturesList.get(previousIndex));
        }


    }

    public View makeView() {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(Gallery.LayoutParams.MATCH_PARENT,
                Gallery.LayoutParams.MATCH_PARENT));
        return i;
    }
}
