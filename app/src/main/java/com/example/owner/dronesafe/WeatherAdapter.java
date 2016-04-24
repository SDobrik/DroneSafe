package com.example.owner.dronesafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Owner on 2016-04-23.
 */
public class WeatherAdapter extends ArrayAdapter<WeatherInstance> {
    public WeatherAdapter(Context context, ArrayList<WeatherInstance> weathers) {
        super(context, 0, weathers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        WeatherInstance weatherInstance = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, parent, false);
        }

        ImageView ivForecastPreview = (ImageView) convertView.findViewById(R.id.forecast_preview_icon);

        // set forecast icon based on description
        switch (weatherInstance.getDescription()){

            default:
                break;
        }
        //ivForecastPreview.setImageDrawable(getContext().getDrawable(R.drawable.m));
        return convertView;
    }
}
