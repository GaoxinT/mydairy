package com.gx.mydairy.dairy.Util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by GX on 2017/1/13.
 */
public class FormatTime
{
    String str1;
    String str = new SimpleDateFormat("yyyyMMdd").format(new Date());

    public FormatTime(String paramString1)
    {
        str1 = paramString1;

    }

    @SuppressLint({"SimpleDateFormat"})
    public int getDateDays() throws Exception {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date localDate1 = localSimpleDateFormat.parse(this.str1);
        Date localDate2 = localSimpleDateFormat.parse(this.str);


        GregorianCalendar localGregorianCalendar2 = new GregorianCalendar();
        GregorianCalendar localGregorianCalendar1 = new GregorianCalendar();

        localGregorianCalendar1.setTime(localDate1);
        localGregorianCalendar2.setTime(localDate2);

        return (int)((localGregorianCalendar2.getTimeInMillis() - localGregorianCalendar1.getTimeInMillis()) / 86400000L);
    }
}

