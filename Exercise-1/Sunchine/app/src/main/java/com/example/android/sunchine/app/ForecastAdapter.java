package com.example.android.sunchine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.ImageWriter;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.widget.CursorAdapter;
import com.example.android.sunchine.app.data.WeatherContract;

import org.w3c.dom.Text;

/*
 * ForecastAdapter exposes a list of weather forecast
 * from a {@link android.dataBase.cursor} to a {@link android.widget.ListView}
 * */


public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    //Flag to determine if we want to use a separate view for "Today"
    private boolean mUseTodayLayout = true;



    public ForecastAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
    }





    /*
    *  Remember  that these view are  reused as needed
    * */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        //Chose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        switch (viewType) {
            case VIEW_TYPE_TODAY : {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
    *  This is where  we fill in the views with the contents of the cursor
    * */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our  view  is pretty simple  here -- just a text view
        // we'll keep the UI functional with a simple (and slow!)binding.


        ViewHolder viewHolder = (ViewHolder)view.getTag();

        //Use placeholder image for now
        int viewType = getItemViewType(cursor.getPosition());

        switch (viewType) {
            case VIEW_TYPE_TODAY : {
                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(
                        cursor.getInt(ForeCastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(
                        cursor.getInt(ForeCastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            }
        }


        //Read date from cursor
        long dateInMillis = cursor.getLong(ForeCastFragment.COL_WEATHER_DATE);
        //Find Textview and set formatted date to it
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));


        //Read weather forecast from cursor
        String description = cursor.getString(ForeCastFragment.COL_WEATHER_DESC);
        //Find textview and set weather forecast on it
        viewHolder.descriptionView.setText(description);

        //for accessibility, add  a content description to the icon field
        viewHolder.iconView.setContentDescription(description);


        //Read user preference for metric or imperial units
        boolean isMetric = Utility.isMetric(context);


        // Read high temperature from cursor
        double high = cursor.getDouble(ForeCastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context,high));

        //Read low temperature from cursor
        double low = cursor.getDouble(ForeCastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context,low));


    }


    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position){
        return position == 0 && mUseTodayLayout ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount(){
        return VIEW_TYPE_COUNT;
    }


}
