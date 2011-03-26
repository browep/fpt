package com.github.browep.fpt.util;

import com.github.browep.fpt.C;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 3/12/11
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class FptTimeFormat extends Format {
  /**
   *
   * @param o should be millis of time
   * @param stringBuffer
   * @param fieldPosition
   * @return
   */
  @Override
  public StringBuffer format(Object o, StringBuffer stringBuffer, FieldPosition fieldPosition) {

    Number number = (Number) o;
    int totalMillis = number.intValue();
    // get hours

    int hours = totalMillis/C.MILLIS_IN_HOURS;

    totalMillis = totalMillis % C.MILLIS_IN_HOURS;

    // get minutes

    int minutes = totalMillis/C.MILLIS_IN_MINUTES;

    totalMillis = totalMillis%C.MILLIS_IN_MINUTES;

    // get seconds

    int seconds = totalMillis / 1000;

    StringBuffer buffer;
    if (hours > 0)
      buffer = (new StringBuffer()).append(hours).append(":").append(String.format("%02d", minutes));
    else
      buffer = new StringBuffer(minutes);

    return buffer.append(":").append(String.format("%02d", seconds));

  }

  @Override
  public Object parseObject(String s, ParsePosition parsePosition) {
    return s;
  }
}
