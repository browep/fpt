package com.github.browep.fpt.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import com.github.browep.fpt.C;
import com.github.browep.fpt.dao.Storable;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public static void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Bitmap decodeFile(File f) {
        try {

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_SIZE = 256;

            //Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

  public static int[] splitToHoursMinSec(Number number) {
    int totalMillis = number.intValue();
    // get hours

    int hours = totalMillis/ C.MILLIS_IN_HOURS;

    totalMillis = totalMillis % C.MILLIS_IN_HOURS;

    // get minutes

    int minutes = totalMillis/ C.MILLIS_IN_MINUTES;

    totalMillis = totalMillis% C.MILLIS_IN_MINUTES;

    // get seconds

    int seconds = totalMillis / 1000;

    int[] values = new int[3];
    values[0] = hours;
    values[1] = minutes;
    values[2] = seconds;
    return values;
  }

  static Comparator<Storable> byModifiedComparator = new Comparator<Storable>(){
    public int compare(Storable storable, Storable storable1) {
      return storable.getCreated().compareTo(storable1.getCreated());
    }
  };

  public static List<Storable> sortByModified(List<Storable> storables){
    Collections.sort(storables, byModifiedComparator );
    return storables;
  }
}
