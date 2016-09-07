package com.example.android.sunchine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunchine.app.gcm.RegistrationIntentService;
import com.example.android.sunchine.app.sync.SunshineSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String DETAILFRAGMENT_TAG  = "DFTAG";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public final static String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    private boolean mTwoPane;
    private String  mLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(findViewById(R.id.weather_detail_container) != null){
            //The detail container will be present only in the large-screen layouts
            //(res/layout-sw600dp). If this view is present, then the activity should be
            //in two-pane mode.
            mTwoPane = true;

            // In two pane mode,show the detail view in this activity by
            //adding or replacing the fragment using a fragment manager
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DailyActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();

            }
        }else{
            mTwoPane = false;
            getSupportActionBar().setElevation(0);
        }

        ForeCastFragment foreCastFragment = ((ForeCastFragment)getSupportFragmentManager()
                 .findFragmentById(R.id.fragment_forecast));


        foreCastFragment.setUseTodayLayout(!mTwoPane);
        SunshineSyncAdapter.initializeSyncAdapter(this);

        //if google  Play services is up to date, we'll want to register GCM. If it not, we'll
        //skip the registration and this device will not receive any downstream  messages from
        //our fake server. Because weather alert are not a core feature in this app, this should
        // not affect the behavior of the app, from the user perspective
        if(checkPlayServices()){
            //Because this is the initial creation of the app, we'll want to be certain we have
            //a token.If we to not,then we will, start the IntentService that will register this
            //application with GCM.
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
            if(!sentToken){
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SttingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onResume(){
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        //update the location in our second  pane using the fragment manager
        if(location != null && !location.equals(mLocation)){
            ForeCastFragment ff = (ForeCastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(null != ff){
                ff.onLocationChanged();
            }
            mLocation = location;
        }



    }



    /*
    * Check the device to make sure is has the Google Play Services APK.  if
    * it doesn't, display a dialog that allows users to download the APK from
    * the Google Play Store or enable it in the device's system settings.
    *
    * */


    private boolean checkPlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else{
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }

        return  true;

    }








}
