package pepperweather.pepperweather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;


import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A placeholder fragment containing a simple view.
 */
public class Tab2 extends TabFragment {

    private final String TAG = Tab2.class.getSimpleName();

    MyAdapter adapter;

    Handler handler;

    public Tab2(){
        handler = new Handler();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2, container, false);

        //Ejecuto inmediatamente el metodo asyncronico para traer la lista de datos del tiempo,
        // sobreescribiendo la informacion harcodeada
        new FetchWeatherTask().execute("94043");

        List<Weather> forecast = new ArrayList<>();

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        adapter=new MyAdapter(getActivity(),R.layout.list_row,forecast);
        listView.setAdapter(adapter);

        return rootView;
    }


    /**
     * FetchWeatherTask AsyncTask
     */
    public class FetchWeatherTask extends AsyncTask<String, Void, List<Weather>> {

        private String TAG = FetchWeatherTask.class.getSimpleName();

        String postCode;
        String format = "json";
        String units = "metric";
        int days = 7;

        @Override
        protected List<Weather> doInBackground(String... strings) {
            postCode = strings[0];

            String forecastJson = null;
            try {
                forecastJson = getForecastJson();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                if(forecastJson == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } else {

                    return getWeatherDataFromJson(forecastJson, days);

                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Weather> results) {
            if(results != null) {
                adapter.clear();
                adapter.setItems(results);
            }
        }
        public static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&cnt=7&appid=6e8a9626d02c7c26f0d754fa9ccb1a49";

        public String getForecastJson() throws MalformedURLException {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            URL finalurl = new URL(String.format(FORECAST_BASE_URL, CityPreference.instance.getCity()));


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(finalurl.toString());

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
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            return forecastJsonStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<Weather> getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_DETAILS = "id";
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.


            //transform your calendar to a long in the way you prefer


            // now we work exclusively in UTC
            Calendar gc = new GregorianCalendar();

            List<Weather> resultList = new ArrayList<Weather>(numDays);

            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                Integer description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                day = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);

                //iterating to the next day
                gc.add(Calendar.DAY_OF_WEEK, 1);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getInt(OWM_DETAILS);

                int id = description / 100;
                String icon = "";

                if(description == 800){
                    icon = getActivity().getString(R.string.weather_sunny);

                } else {
                    switch(id) {
                        case 2 : icon = getActivity().getString(R.string.weather_thunder);
                            break;
                        case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                            break;
                        case 7 : icon = getActivity().getString(R.string.weather_foggy);
                            break;
                        case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                            break;
                        case 6 : icon = getActivity().getString(R.string.weather_snowy);
                            break;
                        case 5 : icon = getActivity().getString(R.string.weather_rainy);
                            break;
                    }
                }

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_DESCRIPTION);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                Weather weather = new Weather();
                weather.day = day;
                weather.icon = icon;
                weather.high = high;
                weather.low = low;

                resultList.add(i, weather);
            }

            return resultList;

        }
    }
    public void changeCity(String city) {
        new FetchWeatherTask().execute("94043");
    }
}