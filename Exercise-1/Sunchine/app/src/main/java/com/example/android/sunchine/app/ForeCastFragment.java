package com.example.android.sunchine.app;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;




import com.example.android.sunchine.app.data.WeatherContract;
import com.example.android.sunchine.app.sync.SunshineSyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForeCastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = ForeCastFragment.class.getName();

    private static final int FORECAST_LOADER = 0;
    private ForecastAdapter mforecastAdapter;
    private boolean mUseTodayLayout;

    //For the forecast view  we're showing only a small subset of the stored data.
    //Specify  the columns we need.

    private static final String[] FORECAST_COLUMNS = {
            // In  this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            //(both have an _id column
            //On the on hand, that's annoying. On the other, you can search the weather table
            //using the location set by the user, witch  is only in the location table
            //So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG

    };

    //These indices are tied to FORECAST_COLUMNS,. if FORECAST_COLUMNS changes, these
    //must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;


    /*
    * A callback interface that all activities containing this fragment must
     *implement
    * */




    public ForeCastFragment() {


    }


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        //line to handle menu events
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void updateWeather(){

        SunshineSyncAdapter.syncImmediately(getActivity());

    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String locationSetting = Utility.getPreferredLocation(getActivity());

        //The CursorAdapter will take data from our cursor and populate the ListView
        mforecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        // get the reference to the  listview, and  attach this adapter to it
        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mforecastAdapter);

        // We' call our  MainActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //CursorAdapter  returns the cursor at the correct position for the getItem(),  pr null
                //if it cannot seek to that position.

                Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);
                if(cursor != null){
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DailyActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);

                }
            }
        });

        mforecastAdapter.setUseTodayLayout(mUseTodayLayout);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    //Since we read the Location when we create the loader , all we need to do is restart
    // things
    void onLocationChanged(){
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        //Sort order: Ascending by date
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mforecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mforecastAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout = useTodayLayout;
        if(mforecastAdapter != null){
            mforecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(getActivity());
        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        if ( null != mforecastAdapter ) {
            Cursor c = mforecastAdapter.getCursor();
            if ( null != c ) {
                c.moveToPosition(0);
                String posLat = c.getString(COL_COORD_LAT);
                String posLong = c.getString(COL_COORD_LONG);
                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

                Uri geoTesTLocation = Uri.parse("geo:0,0?").buildUpon()
                        .appendQueryParameter("q", location)
                        .build();


                Log.d(LOG_TAG, "Geo -> " + geoLocation.toString());
                Log.d(LOG_TAG, "Test Geo -> " + geoTesTLocation.toString());

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
            }

          }
        }











}












