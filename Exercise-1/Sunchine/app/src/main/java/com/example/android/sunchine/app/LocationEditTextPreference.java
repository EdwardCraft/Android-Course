package com.example.android.sunchine.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.support.v4.app.LoaderManager;
import android.util.AttributeSet;

/**
 * Created by PEDRO on 17/08/2016.
 */
public class LocationEditTextPreference extends EditTextPreference {
    static final private int DEFAULT_MINIMUM_LOCATION_LENGTH = 2;
    private int mMinLength;


    public LocationEditTextPreference(Context context, AttributeSet atts){
        super(context, atts);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                atts,
                R.styleable.LocationEditTextPreference,
                0, 0
        );

        try {
            mMinLength = a.getInteger(R.styleable.LocationEditTextPreference_minLength,
                    DEFAULT_MINIMUM_LOCATION_LENGTH);
        }finally {
            a.recycle();
        }


    }





}
