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
public class FptDateFormat extends Format {
    @Override
    public StringBuffer format(Object o, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        // assuming this is going to be a number because thats all we will be sending in
        Number number = (Number) o;
        Date date = new Date(number.longValue());

        return stringBuffer.append((date.getMonth()+1)+ "/" + date.getDate() );

    }

    @Override
    public Object parseObject(String s, ParsePosition parsePosition) {
        return s;
    }
}
