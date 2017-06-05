package pepperweather.pepperweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<Weather> {

    public MyAdapter(Context context, int textViewResourceId, List<Weather> objects) {
        super(context, textViewResourceId, objects);
    }

    public void setItems(List<Weather> items) {

        super.clear();
        super.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Weather weather = super.getItem(position);

        View v;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_row, null);
        TextView day = (TextView) v.findViewById(R.id.weather_day);
        TextView weather_icon = (TextView) v.findViewById(R.id.weather_icon);
        TextView maxMin = (TextView) v.findViewById(R.id.weather_maxMin);
        day.setText(weather.day);
        weather_icon.setText(weather.icon);
        maxMin.setText(formatHighLows(weather.high, weather.low));
        return v;

    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh+"°/" + roundedLow+"°";
        return highLowStr;
    }
}