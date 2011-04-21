package com.github.browep.fpt;

import android.os.AsyncTask;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 4/19/11
 * Time: 11:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateImagesTask extends AsyncTask<FptApp, Void, Integer> {
  @Override
  protected Integer doInBackground(FptApp... fptApps) {

    fptApps[0].makeFptPicturesOutOfExistingFiles();
    fptApps[0].uploadAnyNonUploadedPics();
    return null;
  }
}
