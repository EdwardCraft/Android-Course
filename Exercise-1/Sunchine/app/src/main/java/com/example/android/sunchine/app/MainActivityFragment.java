package com.example.android.sunchine.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    private ArrayAdapter<String> mforecastAdapter;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        String[] forecastArray = {
                "Today - Sunny -88/63",
                "Today - Sunny -88/63",
                "Today - Sunny -88/63",
                "Today - Sunny -88/63",
                "Today - Sunny -88/63",
                "Today - Sunny -88/63",
        };

        List<String> weekForCast = new ArrayList<String>(
                Arrays.asList(forecastArray));


        mforecastAdapter = new ArrayAdapter<String>(
                //The current context (this fragment's parent activity )
                getActivity(),
                // ID if the list item layout
                R.layout.list_item_forecast,
                //ID of the textview to populate
                R.id.list_item_forecast_textview,
                //Forecast Data
                weekForCast
        );

        // get the reference to the  listview, and  attach this adapter to it
        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mforecastAdapter);





        return rootView;
    }
}
