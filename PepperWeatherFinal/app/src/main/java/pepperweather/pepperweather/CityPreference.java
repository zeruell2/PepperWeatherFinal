package pepperweather.pepperweather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class CityPreference {

    private enum Keys {

        CITY("city");

        private String stringValue;

        Keys(String stringValue) {
            this.stringValue = stringValue;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public Keys getEnum(String stringValue) {

            if (stringValue == null) {
                throw new IllegalArgumentException();
            }

            for (Keys key : values()) {

                if (stringValue.equalsIgnoreCase(key.toString())) {
                    return key;
                }
            }

            throw new IllegalArgumentException();
        }
    }

    public static CityPreference instance = new CityPreference();

    private SharedPreferences prefs;
    public Context context;

    private CityPreference() {

    }

    public CityPreference setupWithActivity(Activity activity) {
        context = activity;
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);

        return this;
    }

    public String getCity(){

        if (context == null) {
            throw new IllegalStateException("Activty not set.");
        }

        return prefs.getString(Keys.CITY.toString(), context.getResources().getString(R.string.default_city));
    }

    public void setCity(String city){

        if (context == null) {
            throw new IllegalStateException("Activty not set.");
        }

        prefs.edit().putString(Keys.CITY.toString(), city).apply();
    }

}