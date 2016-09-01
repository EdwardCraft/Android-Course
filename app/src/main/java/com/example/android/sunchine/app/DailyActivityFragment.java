package com.example.android.sunchine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sunchine.app.data.WeatherContract;
import com.example.android.sunchine.app.data.WeatherContract.WeatherEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DailyActivityFragment extends Fragment implements LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DailyActivityFragment.class.getName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";


    private ShareActionProvider mShareActionProvider;
    private String  mForecast;


    private static final int DETAIL_LOADER = 0;
    private ViewHolderDetail viewHolderDetail;


    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            //This works because the Weather Provider returns location data joined with
            //weather data, even though they're stored in two different tables
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    //These constants correspond to the projection defined above,and must change if the
    //projection changes

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;







    public DailyActivityFragment() {

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detaifragment, menu);

        //Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        //Get the provider and hold onto it  to set/change the share intent;
        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(menuItem);

        if(mForecast != null){
            mShareActionProvider.setShareIntent(createShareForeCastIntent());
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_daily, container, false);
        viewHolderDetail = new ViewHolderDetail(rootView);

        return rootView;
    }


    private Intent createShareForeCastIntent(){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }






    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if(intent == null || intent.getData() == null){
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        //creating a cursor fot the data being displayed.

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if(data != null && data.moveToFirst()){
                //Read weather condition ID form cursor
                int weatherID = data.getInt(COL_WEATHER_CONDITION_ID);
                //use weather image
                /*viewHolderDetail.mIconView.setImageResource(
                        Utility.getArtResourceForWeatherCondition(weatherID));*/

                //use Glide to get image
                Glide.with(this)
                        .load(Utility.getArtUrlForWeatherCondition(getActivity(), weatherID))
                        .error(Utility.getArtResourceForWeatherCondition(weatherID))
                        .crossFade()
                        .into(viewHolderDetail.mIconView);


                //Read date from cursor and update views for day of week and date
                long date = data.getLong(COL_WEATHER_DATE);
                String friendlyDateText = Utility.getDayName(getActivity(), date);
                String dateText = Utility.getFormattedMonthDay(getActivity(), date);
                viewHolderDetail.mFriendlyDateView.setText(friendlyDateText);
                viewHolderDetail.mDateView.setText(dateText);

                //Read description from cursor and update view
                String description = data.getString(COL_WEATHER_DESC);
                viewHolderDetail.mDescriptionView.setText(description);

                // for accessibility, add a content description to the icon field
                viewHolderDetail.mIconView.setContentDescription(description);



                //Read high temperature from the cursor and update the view
                boolean isMetric = Utility.isMetric(getActivity());
                double high = data.getDouble(COL_WEATHER_MAX_TEMP);
                String highString = Utility.formatTemperature(getActivity(), high);
                viewHolderDetail.mHighTempView.setText(highString);

                //Read low temperature from cursor and update view
                double low = data.getDouble(COL_WEATHER_MIN_TEMP);
                String lowString = Utility.formatTemperature(getActivity(), low);
                viewHolderDetail.mLowTempView.setText(lowString);

                //Read humidity from cursor and update view
                float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
                viewHolderDetail.mHumidityView.setText(
                        getActivity().getString(R.string.format_humidity, humidity));

                //Read wind speed and direction from cursor and update view
                float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
                float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
                viewHolderDetail.mWindView.setText(
                        Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

                //Read pressure from cursor and update view
                float pressure = data.getFloat(COL_WEATHER_PRESSURE);
                viewHolderDetail.mPressuredView.setText(
                        getActivity().getString(R.string.format_pressure, pressure));

                //We still need this for the share intent

                mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

                // If onCreateOptionsMenu has already happened, we need to update the share intent now
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareForeCastIntent());
                }

            }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
