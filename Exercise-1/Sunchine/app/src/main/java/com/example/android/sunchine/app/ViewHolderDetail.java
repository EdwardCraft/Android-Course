package com.example.android.sunchine.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by PEDRO on 08/08/2016.
 */
public class ViewHolderDetail {


    //View Holder for the detail Fragment
    public final ImageView mIconView;
    public final TextView mFriendlyDateView;
    public final TextView mDateView;
    public final TextView mDescriptionView;
    public final TextView mHighTempView;
    public final TextView mLowTempView;
    public final TextView mHumidityView;
    public final TextView mWindView;
    public final TextView mPressuredView;


    public ViewHolderDetail(View view){
        mIconView = (ImageView)view.findViewById(R.id.detail_icon);
        mDateView = (TextView)view.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView)view.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView)view.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView)view.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView)view.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView)view.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView)view.findViewById(R.id.detail_wind_textview);
        mPressuredView = (TextView)view.findViewById(R.id.detail_pressure_textview);
    }


}
