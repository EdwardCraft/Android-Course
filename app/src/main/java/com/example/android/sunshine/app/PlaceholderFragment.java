package com.example.android.sunshine.app;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceholderFragment extends Fragment {
    public static final  String LOG_TAG = PlaceholderFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";


    private String forecastStr;

    public PlaceholderFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra("DATA")){
            forecastStr = intent.getStringExtra("DATA");
            TextView textView = (TextView)rootView.findViewById(R.id.intent_textview);
            textView.setText(forecastStr);
        }



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Inflate the menu: this add item to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        //Retrieve the share menu item
        MenuItem menuItem =  menu.findItem(R.id.action_share);

        //Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        //Attach an intent to this.  You can  update this at  any time,
        //like when the user selects  a new piece  of data they might like to share
        if(shareActionProvider != null){
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }else{
            Toast toast = Toast.makeText(getActivity(),  "Share Action Provider is null?", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                forecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }


}
