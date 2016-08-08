package com.example.android.sunchine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PEDRO on 07/08/2016.
 */
public class Utility {

    public static String getPreferredLocation(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_Temperature_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    static String formatTemperature(Context context,double temperature, boolean isMetric){
        double temp;
        if( !isMetric ){
            temp = 9 * temperature / 5 + 32;
        }else{
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }



    static String formatDate(long dateInMilliseconds){
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }



    //Format used fore storing dates in the databases. Also uses for converting those strings
    //back into date objects for comparison/processing
    public static final String DATE_FORMAT = "yyyyMMdd";

    /*
    * Helper method to convert the database representation of the data into something to display
    * to users. As classy  and  polished a user experience as "20140802" is, we can do better.
    * */

    public static String getFriendlyDayString(Context context, long dateInMillis){
        //The day string for forecast uses the following logic
        //For today: "Today, June 8"
        //for Tomorrow: "Tomorrow"
        //for the next 5 days: "Wednesday" (just the day name)
        //for all days after that: "Mon jun 8"

        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        //if the date we're building the string for is today's date, the format
        //is "Today, June 24"

        if(julianDay == currentJulianDay){
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(context, dateInMillis)));
        }else if(julianDay < currentJulianDay + 7){
            //if the input date is less than a week  in the future, just return the day name
            return getDayName(context, dateInMillis);
        }else{
            //Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);

        }

    }

    /*
    * Given a day, returns  just the name  to use  for that day
    * R. g. "today", "tomorrow", "wednesday"
    * */

    public static String getDayName(Context context, long dateInMillis){
        // If the date is today, return the locations version of "today" instead of the actual
        // day name

        Time t = new Time();
        t.setToNow();

        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDate = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if(julianDay == currentJulianDate){
            return context.getString(R.string.today);
        }else if(julianDay == currentJulianDate + 1){
            return context.getString(R.string.tomorrow);
        }else {
            Time time = new Time();
            time.setToNow();
            //Otherwise, the format is just  the day  of the week  (e.g "wednesday")
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }

    }

    /*
    * Converts db date format to the format "Month day" e.g "June 24"
    *
    * */

    public static String getFormattedMonthDay(Context context, long dateInMillis){
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }


    public static String getFormattedWind(Context  context, float windSpeed, float degrees){
        int windFormat;
        if(Utility.isMetric(context)){
            windFormat = R.string.format_wind_kmh;
        }else{
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        //From wind direction in degrees, determine compass direction as a string
        //you know what's fun, writing really long if/else statements with tons of
        //possible conditions.
        String direction = "unknown";
        if(degrees >= 337.5 || degrees < 22.5){
            direction = "N";
        }else if(degrees >= 22.5 && degrees < 67.5){
            direction = "NE";
        }else if(degrees >= 67.5 && degrees < 112.5){
            direction = "E";
        }else if(degrees >= 112.5 && degrees < 157.5){
            direction = "SE";
        }else if(degrees >= 157.5 && degrees < 202.5){
            direction = "S";
        }else if(degrees >= 202.5 && degrees < 247.5){
            direction = "SW";
        }else if(degrees >= 247.5 && degrees < 292.5){
            direction = "W";
        }else if(degrees >= 292.5 && degrees < 337.5){
            direction = "NW";
        }


        return String.format(context.getString(windFormat), windSpeed, direction);

    }











}

