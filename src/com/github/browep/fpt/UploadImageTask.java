package com.github.browep.fpt;

import android.os.AsyncTask;
import android.util.Base64;
import com.github.browep.fpt.util.Log;
import com.github.browep.fpt.util.Util;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 4/11/11
 * Time: 12:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class UploadImageTask extends AsyncTask<File, Void, String> {
  protected String doInBackground(File... files) {

    File imageFile = files[0];
    Log.i("starting upload for " + imageFile);


    try {
      // Creates Byte Array from picture
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      URL url = new URL("http://api.imgur.com/2/upload.json");

      //encodes picture with Base64 and inserts api key
      String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encode(Util.getBytesFromFile(imageFile), Base64.DEFAULT).toString(), "UTF-8");
      data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(C.IMGUR_API_KEY, "UTF-8");

      // opens connection and sends data
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
//      conn.setRequestProperty("Accept","*/*");

      conn.setDoOutput(true);
      conn.setDoInput(true);
      OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());

      out.write(data);
      out.close();

      InputStream inputStream = conn.getErrorStream();
//      InputStream inputStream = conn.getErrorStream();
      InputStreamReader inputStreamReader = new InputStreamReader(
          inputStream);
      BufferedReader in = new BufferedReader(
          inputStreamReader);

      String decodedString;
      Log.i("reading from imgur input stream");
      while ((decodedString = in.readLine()) != null) {
        Log.i(decodedString);
      }
      in.close();
      return null;

    } catch (IOException e) {
      Log.e("error uploading " + imageFile.getAbsolutePath(), e);
    }
    return null;
  }

  protected void onPostExecute(File result) {
  }
}
//
//  URL url = new URL("http://api.imgur.com/2/upload");
//
//         // + URLEncoder.encode(new String(Base64.encode(Util.getBytesFromFile(imageFile), Base64.DEFAULT)), "UTF-8");
//         String data = URLEncoder.encode("image", "UTF-8") + "=" ;
//
//         BufferedInputStream bis = new BufferedInputStream( new FileInputStream( imageFile ) );
//
//
//         URLConnection conn = url.openConnection();
//         BufferedReader in = new BufferedReader(
//             new InputStreamReader(
//                 conn.getInputStream()));
//
//         conn.setDoOutput(true);
//         OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//
//         wr.write(data);
//         int i;
//            // read byte by byte until end of stream
//            while ((i = bis.read()) != -1)
//            {
//               wr.write(i);
//            }
//
//         wr.write("&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(C.IMGUR_API_KEY, "UTF-8"));
//
//         wr.flush();