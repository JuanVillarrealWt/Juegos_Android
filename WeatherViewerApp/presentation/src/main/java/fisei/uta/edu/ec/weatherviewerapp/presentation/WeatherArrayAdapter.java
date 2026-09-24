package fisei.uta.edu.ec.weatherviewerapp.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fisei.uta.edu.ec.weatherviewerapp.domain.entities.Weather;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/** ListView renderer and icon cache; formatting belongs to the presentation layer. */
public final class WeatherArrayAdapter extends ArrayAdapter<Weather> {
    private final Map<String, Bitmap> bitmaps = new HashMap<>();
    private static final class Holder {
        ImageView icon; TextView day; TextView low; TextView high; TextView humidity;
    }
    public WeatherArrayAdapter(Context context, List<Weather> forecast) { super(context, -1, forecast); }

    @Override public View getView(int position, View recycled, ViewGroup parent) {
        Holder holder;
        if (recycled == null) {
            recycled = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new Holder();
            holder.icon = recycled.findViewById(R.id.conditionImageView);
            holder.day = recycled.findViewById(R.id.dayTextView);
            holder.low = recycled.findViewById(R.id.lowTextView);
            holder.high = recycled.findViewById(R.id.hiTextView);
            holder.humidity = recycled.findViewById(R.id.humidityTextView);
            recycled.setTag(holder);
        } else holder = (Holder) recycled.getTag();

        Weather weather = getItem(position);
        if (weather != null) {
            String iconUrl = "https://openweathermap.org/img/w/" + weather.iconCode + ".png";
            Bitmap bitmap = bitmaps.get(iconUrl);
            if (bitmap != null) holder.icon.setImageBitmap(bitmap);
            else { holder.icon.setImageDrawable(null); new LoadIconTask(holder.icon, iconUrl).execute(); }
            holder.day.setText(getContext().getString(R.string.day_description, dayName(weather.timestamp), weather.description));
            holder.low.setText(getContext().getString(R.string.low_temp, temperature(weather.minTemperature)));
            holder.high.setText(getContext().getString(R.string.high_temp, temperature(weather.maxTemperature)));
            holder.humidity.setText(getContext().getString(R.string.humidity,
                    NumberFormat.getPercentInstance().format(weather.humidity / 100.0)));
        }
        return recycled;
    }

    private String temperature(double value) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(0);
        return format.format(value) + "\u00B0F";
    }

    private String dayName(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);
        TimeZone zone = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, zone.getOffset(calendar.getTimeInMillis()));
        String day = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(calendar.getTime());
        return day.isEmpty() ? day : day.substring(0, 1).toUpperCase() + day.substring(1);
    }

    private final class LoadIconTask extends AsyncTask<Void, Void, Bitmap> {
        private final ImageView target;
        private final String address;
        LoadIconTask(ImageView target, String address) { this.target = target; this.address = address; }
        @Override protected Bitmap doInBackground(Void... ignored) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(address).openConnection();
                try (InputStream stream = connection.getInputStream()) { return BitmapFactory.decodeStream(stream); }
            } catch (Exception ignoredException) { return null; }
            finally { if (connection != null) connection.disconnect(); }
        }
        @Override protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) { bitmaps.put(address, bitmap); target.setImageBitmap(bitmap); }
        }
    }
}
