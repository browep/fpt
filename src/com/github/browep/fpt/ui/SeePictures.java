package com.github.browep.fpt.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.github.browep.fpt.C;
import com.github.browep.fpt.R;
import com.github.browep.fpt.dao.DaoAwareActivity;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SeePictures extends DaoAwareActivity implements ViewPager.OnPageChangeListener{

    private static final String MORE_PICTURES_MESSAGE = "Take more pictures!";
    private Bitmap nextBitmap;
    private Bitmap previousBitmap;
    private Button previousButton;
    private Button nextButton;
    private Integer index = 0;
    private List<File> picturesList;
    private SeePictures self = this;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_pictures);

        previousButton = (Button) findViewById(R.id.previous_button);
        nextButton = (Button) findViewById(R.id.next_button);

        previousButton.setOnClickListener(previousButtonOnClickListener);
        nextButton.setOnClickListener(nextButtonOnClickListener);


        // get the earliest picture

        File pictureDirectory = new File(Util.getThumbsDirectory());
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
            updateTextDislay(pictureFile);

            viewPager = (ViewPager) findViewById(R.id.view_pager);
            viewPager.setAdapter(new PicturePagerAdapter());
            viewPager.setOnPageChangeListener(this);

        } else {
            Util.longToastMessage(this, "Need to add pictures first. Click \"Add a Progress Picture\" on the welcome screen first");
            finish();
        }


        getFptApplication().getTracker().trackEvent(
                "Picture",  // Category
                "Viewed",  // Action
                null, // Label
                0);

    }


    View.OnClickListener nextButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (picturesList.size() > 1) {

                if (viewPager.getCurrentItem() < picturesList.size() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
                }

            } else {
                Util.longToastMessage(self, MORE_PICTURES_MESSAGE);
            }

        }
    };


    View.OnClickListener previousButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (picturesList.size() > 1) {

                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
                }

            } else {
                Util.longToastMessage(self, MORE_PICTURES_MESSAGE);
            }
        }
    };


    private void updateTextDislay(File file) {
        try {
            String name = file.getName().replace(".jpg", "");
            Date date = new Date(Long.parseLong(name));
            int dateDelta = (int) (((new Date()).getTime() - date.getTime()) / C.MILLIS_IN_A_DAY);
            TextView pictureTitle = (TextView) findViewById(R.id.picture_title);
            String title = dateDelta == 0 ? "Today" : dateDelta + " day" + (dateDelta == 1 ? "" : "s") + " ago";
            pictureTitle.setText(title);
        } catch (Exception e) {
            Log.e("error with " + file != null ? file.getAbsolutePath() : "null file", e);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("Delete Picture");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // open up the edit screen
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setCancelable(true);
        builder.setTitle("Confirm Delete");
        builder.setInverseBackgroundForced(true);
        builder.setMessage("Are you sure you want to delete this picture?  There is no UNDO!");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                File currentFileThumb = picturesList.get(viewPager.getCurrentItem());
                Log.i("deleting " + currentFileThumb.getAbsolutePath());
                currentFileThumb.delete();
                // get saved file
                File currentFile = new File(currentFileThumb.getAbsolutePath().replace("/thumbs", ""));
                Log.i("deleting " + currentFile.getAbsolutePath());
                currentFile.delete();

                Util.longToastMessage(self, "Picture Deleted.");
                finish();

            }
        });

        builder.setNegativeButton("Don't Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }


    @Override
    public String getPageName() {
        return "SeePictures";
    }

    public void onPageScrolled(int i, float v, int i1) {

    }

    public void onPageSelected(int i) {
        File file = picturesList.get(i);
        Log.i("setting to file " + file.getName());
        updateTextDislay(file);
    }

    public void onPageScrollStateChanged(int i) {

    }

    private class PicturePagerAdapter extends PagerAdapter {

        public PicturePagerAdapter() {

        }

        @Override
        public int getCount() {
            return picturesList.size();
        }

        /**
         * Create the page for the given position.  The adapter is responsible
         * for adding the view to the container given here, although it only
         * must ensure this is done by the time it returns from
         * {@link #finishUpdate()}.
         *
         * @param container The containing View in which the page will be shown.
         * @param position The page position to be instantiated.
         * @return Returns an Object representing the new page.  This does not
         * need to be a View, but can be some other container of the page.
         */
        @Override
        public Object instantiateItem(View collection, int position) {

            ImageView imageView = new ImageView(SeePictures.this);

            String absolutePath = picturesList.get(position).getAbsolutePath();

            Log.d("instantiating " + position + " at " + absolutePath);

            imageView.setImageDrawable(new BitmapDrawable(absolutePath));

            ((ViewPager) collection).addView(imageView, 0);


            return imageView;
        }

        /**
         * Remove a page for the given position.  The adapter is responsible
         * for removing the view from its container, although it only must ensure
         * this is done by the time it returns from {@link #finishUpdate()}.
         *
         * @param container The containing View from which the page will be removed.
         * @param position The page position to be removed.
         * @param object The same object that was returned by
         * {@link #instantiateItem(View, int)}.
         */
        @Override
        public void destroyItem(View collection, int position, Object view) {

            ImageView imageView = (ImageView) view;
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            if (bitmapDrawable != null) {
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if(bitmap != null){
                    bitmap.recycle();
                }
            }
            ((ViewPager) collection).removeView(imageView);
        }



        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==(object);
        }


        /**
         * Called when the a change in the shown pages has been completed.  At this
         * point you must ensure that all of the pages have actually been added or
         * removed from the container as appropriate.
         * @param container The containing View which is displaying this adapter's
         * page views.
         */
        @Override
        public void finishUpdate(View arg0) {}


        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {}

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {}

    }
}
