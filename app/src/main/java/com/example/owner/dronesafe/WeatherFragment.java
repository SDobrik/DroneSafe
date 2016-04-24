package com.example.owner.dronesafe;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {

    private WeatherAdapter mForecastAdapter;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param location the location to find weather at
     * @return A new instance of fragment WeatherFragment.
     */
    public static WeatherFragment newInstance(LatLng location) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putParcelable("current_location", location);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute((LatLng) getArguments().getParcelable("current_location"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View weatherView = inflater.inflate(R.layout.fragment_weather, container, false);

        mForecastAdapter = new WeatherAdapter(getActivity(), new ArrayList<WeatherInstance>());
        // set array adapter?
        TwoWayView twoWayView = (TwoWayView) weatherView.findViewById(R.id.forecast_display);
        twoWayView.setAdapter(mForecastAdapter);
        twoWayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                previewWeather(weatherView, position);
            }
        });

        return weatherView;
    }

    void previewWeather(View weatherView, int position) {
        // pass data to big text and photo
        WeatherInstance weatherInstance = mForecastAdapter.getItem(position);

        ((ImageView) weatherView.findViewById(R.id.weather_icon_display))
                .setImageResource(WeatherAdapter.getDrawableId(weatherInstance.getDescription()));
        ((TextView) weatherView.findViewById(R.id.temperature_display))
                .setText(weatherInstance.getTemperature());
        ((TextView) weatherView.findViewById(R.id.wind_speed_display))
                .setText(weatherInstance.getWind());
    }

    // fetchweathertask class w/ doinbackground
    public class FetchWeatherTask extends AsyncTask<LatLng, Void, WeatherInstance[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String formatTemp(double temp, String unitType) {
            String unitDisplay = "°C";
            double temperature = temp;
            if (unitType.equals(getString(R.string.pref_temp_units_fahrenheit))) {
                temperature = temp * 1.8 + 32;
                unitDisplay = "°F";
            } else if (!unitType.equals(getString(R.string.pref_temp_units_celsius))) {
                Log.d(LOG_TAG, "Unit type not found: " + unitType);
            }

            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedTemp = Math.round(temperature);

            return Objects.toString(roundedTemp, null) + unitDisplay;
        }

        private String formatWind(double speed, double dir, String speedUnit){
            String wind="";
            double windSpeed=speed;
            String unitDisplay = "km/h";
            String windDir= Objects.toString(dir) + "°";
            if (speedUnit.equals(getString(R.string.pref_speed_units_mph))) {
                windSpeed = speed / 1.6;
                unitDisplay = "mph";
            } else if (!speedUnit.equals(getString(R.string.pref_speed_units_kmh))) {
                Log.d(LOG_TAG, "Unit type not found: " + speedUnit);
            }


            wind = windSpeed + " " + unitDisplay + " " + windDir;
            return wind;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private WeatherInstance[] getWeatherDataFromJson(String forecastJsonStr, int numCasts)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_WIND = "wind";
            final String OWM_SPEED = "speed";
            final String OWM_DIRECTION = "deg";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            WeatherInstance[] resultStrs = new WeatherInstance[numCasts];

            // Data is fetched in Celsius by default.
            // If user prefers to see in Fahrenheit, convert the values here.
            // We do this rather than fetching in Fahrenheit so that the user can
            // change this option without us having to re-fetch the data once
            // we start storing the values in a database.
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String tempUnit = sharedPrefs.getString(
                    getString(R.string.pref_temp_key),
                    getString(R.string.pref_temp_units_celsius));
            String speedUnit = sharedPrefs.getString(
                    getString(R.string.pref_speed_key),
                    getString(R.string.pref_speed_units_kmh));

            for (int i = 0; i < weatherArray.length(); i++) {
                resultStrs[i] = new WeatherInstance();
                String description;
                String temperature;
                String wind;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_DESCRIPTION);
                double temp = temperatureObject.getDouble(OWM_TEMPERATURE);
                temperature = formatTemp(temp, tempUnit);

                JSONObject windObject = dayForecast.getJSONObject(OWM_WIND);
                double speed = windObject.getDouble(OWM_SPEED);
                double dir = windObject.getDouble(OWM_DIRECTION);
                wind = formatWind(speed,dir,speedUnit);

                resultStrs[i].setTemperature(temperature);
                resultStrs[i].setDescription(description);
                resultStrs[i].setWind(wind);
            }
            return resultStrs;

        }

        @Override
        protected WeatherInstance[] doInBackground(LatLng... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numCasts = 5;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast?";
                final String LAT_PARAM = "lat";
                final String LNG_PARAM = "lon";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String CASTS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(LAT_PARAM, Objects.toString(params[0].latitude))
                        .appendQueryParameter(LNG_PARAM, Objects.toString(params[0].longitude))
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(CASTS_PARAM, Integer.toString(numCasts))
                        .appendQueryParameter(APPID_PARAM, "c367fa013fb044e219ee6cf86d3162b2")
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numCasts);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(WeatherInstance[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                for (WeatherInstance dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
                View weatherView = getActivity().findViewById(R.id.fragment_view);
                previewWeather(weatherView, 0);
            }
        }
    }

}
