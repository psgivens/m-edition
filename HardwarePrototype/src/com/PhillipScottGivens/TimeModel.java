package com.PhillipScottGivens;

import android.content.PeriodicSync;
import android.os.Binder;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 5/22/12
 * Time: 9:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class TimeModel{
    private DateTime startTime;
    private Period elapsedTime;
    public TimeModel(DateTime timeStamp)
    {
        this.startTime = timeStamp;
    }

    public void updateCurrentTime(DateTime currentTime)
    {
        elapsedTime = new Period(startTime, currentTime);
    }

    public Period getElapsedTime()
    {
        return elapsedTime;
    }
}
