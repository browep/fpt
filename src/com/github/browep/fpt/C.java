package com.github.browep.fpt;

import com.github.browep.fpt.util.FptTimeFormat;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/3/11
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class C {


  public static final Integer MILLIS_IN_HOURS = 1000 * 60 * 60;
  public static final Integer MILLIS_IN_MINUTES = 1000 * 60;
  public static final Integer MILLIS_IN_SECONDS = 1000;
  public static final String TIME = "time";
  public static final String DISTANCE = "distance";
  public static final String MAX_WEIGHT = "max_weight";
  public static final String X_VALUE_NAME = "x_value_name";
  public static final String Y_LABEL = "y_label";
  public static final String WORKOUT_ENTRY_ID = "workout_entry_id";
  public static final int PICTURE_TYPE = 6;
  public static final String FILE_NAME = "file_name";
  public static final String IMGUR_API_KEY = "7d30320d0311853f5a0e99fdd08c2001";
  public static final String UPLOAD_HOSTNAME = "fpt.heroku.com";
//  public static final String UPLOAD_HOSTNAME = "192.168.0.189:3000";
  public static final String URL = "url";
  public static final String UPLOADED = "uploaded";
  public static final String AUTHORIZED_FOR_REPORT = "authorized_for_report";
  public static final String TWITTER_SECRET_TOKEN = "twitter_secret_token";
  public static final String TWITTER_ACCESS_TOKEN = "twitter_access_token";
  public static final String TWITTER_CONSUMER_KEY = "V1iOpvQgLdI7D1KpTGKk6A";
  public static final String TWITTER_CONSUMER_SECRET = "sEADmoc96sQqfeS2qMk1L2cIYRX3ThP7Iv4NA15nM";
  public static final String REPORT_EMAIL = "report_email";
  public static final String FACEBOOK_APP_ID = "220215881324575";
  public static final String FACEBOOK_AUTHORIZE_TOKEN = "facebook_authorize_token";
  public static final String FACEBOOK_MARKET_LINK = "http://bit.ly/lelblj"; //http://saml.heroku.com/?id=com.github.browep.fpt&referrer=utm_source%3Dinapp%26utm_medium%3Dfacebook%26utm_term%3Dworkout%26utm_content%3Dmycontent%26utm_campaign%3Dinapp
  public static final String TWITTER_MARKET_LINK = "http://bit.ly/lyAggv"; //http://saml.heroku.com/?id=com.github.browep.fpt&referrer=utm_source%3Dinapp%26utm_medium%3Dtwitter%26utm_term%3Dworkout%26utm_content%3Dmycontent%26utm_campaign%3Dinapp
  public static final String SD_CARD_NOT_MOUNTED_MESSAGE = "Your SD Card is currently unavailable.  Is it present or do you have your phone" +
      " plugged in?";
    public static final String ENTER_DATA_SORT_ORDER = "enter_data_sort_order";
    public static final String SEE_PROGRESS_CHOOSER_ORDER = "see_progress_chooser_order";

    private C() {
  } // should never be used

  public static final String WORKOUT_CLASS = "workout_class";
  public static final Integer FOR_REPS_WORKOUT_TYPE = 10;
  public static final Integer FOR_TIME_WORKOUT_TYPE = 11;
  public static final Integer FOR_DISTANCE_WORKOUT_TYPE = 12;
  public static final Integer FOR_MAX_WEIGHT_WORKOUT_TYPE = 13;

  public static final Integer WORKOUT_DEFINITION_TYPE = 5;

  public static final String WORKOUT_OBJECT = "workout_object";


  public static final Integer[] WORKOUT_TYPES = new Integer[]{FOR_REPS_WORKOUT_TYPE, FOR_TIME_WORKOUT_TYPE, FOR_DISTANCE_WORKOUT_TYPE, FOR_MAX_WEIGHT_WORKOUT_TYPE};
  public static final String WORKOUT_TYPE = "workout_type";
  public static final String WORKOUT_DEFINITION = "workout_definition";
  public static final String WORKOUT_DEFINITION_ID = "workout_definition_id";
  public static final String WORKOUT_NAME = "workout_name";
  public static final String FLURRY_ID = "PJ4JF52Q57JRP84IIGAY";
  public static final String GOOGLE_ANALYTICS_ID = "UA-10324101-13";
  public static final String REPS = "reps";
  public static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("EEEEE, MMMMM dd, yyyy");
  public static final SimpleDateFormat GRAPH_DISPLAY_FORMAT = new SimpleDateFormat("MM/dd/yy");
  public static final Long MILLIS_IN_A_DAY = 86400000L;
  public static final String COMMENT = "comment";

  public static HashMap<Integer, Format> WORKOUT_TYPE_TO_X_FORMAT = new HashMap<Integer, Format>();

  static {
    WORKOUT_TYPE_TO_X_FORMAT.put(C.FOR_TIME_WORKOUT_TYPE, new FptTimeFormat());
  }

  public static final String PREFS_FILE_NAME = "main.prefs";

  public static final String WELCOME_DIALOG = "welcome_dialog";
  public static final String CREATE_WORKOUT_DIALOG = "create_workout_dialog";
  public static final String VIEW_PROGRESS_DIALOG = "view_progress_dialog";
  public static final String EDIT_DATA_DIALOG = "edit_data_dialog";

}
