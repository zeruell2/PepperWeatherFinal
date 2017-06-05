package pepperweather.pepperweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<Item> {

    ArrayList<Item> forecast = new ArrayList<>();

    public MyAdapter(Context context, int textViewResourceId, ArrayList<Item> objects) {
        super(context, textViewResourceId, objects);
        forecast = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_row, null);
        TextView day = (TextView) v.findViewById(R.id.weather_day);
        TextView weather_icon = (TextView) v.findViewById(R.id.weather_icon);
        TextView maxMin = (TextView) v.findViewById(R.id.weather_maxMin);
        day.setText(forecast.get(position).getDay());
        weather_icon.setText(forecast.get(position).getIcon());
        maxMin.setText(forecast.get(position).getMaxMin());
        return v;

    }

}