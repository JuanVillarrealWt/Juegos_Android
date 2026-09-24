package fisei.uta.edu.ec.weatherviewerapp.infrastructure.repositories;

import fisei.uta.edu.ec.weatherviewerapp.application.contracts.repositories.WeatherRepository;
import fisei.uta.edu.ec.weatherviewerapp.domain.entities.Forecast;
import fisei.uta.edu.ec.weatherviewerapp.domain.entities.Weather;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** HTTP and JSON implementation of the weather repository. */
public final class HttpWeatherRepository implements WeatherRepository {
    private final String apiKey;
    private final String openWeatherBaseUrl;

    public HttpWeatherRepository(String apiKey, String openWeatherBaseUrl) {
        this.apiKey = apiKey;
        this.openWeatherBaseUrl = openWeatherBaseUrl;
    }

    @Override public Forecast getForecast(String city) throws Exception {
        if (hasApiKey()) {
            try {
                JSONObject response = request(openWeatherBaseUrl + encode(city)
                        + "&units=imperial&cnt=16&lang=es&APPID=" + encode(apiKey));
                return parseOpenWeather(response, city);
            } catch (Exception ignored) { /* Keep the free Open-Meteo fallback. */ }
        }
        return getOpenMeteoForecast(city);
    }

    private boolean hasApiKey() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("Use your own");
    }

    private Forecast getOpenMeteoForecast(String city) throws Exception {
        JSONObject geocoding = request("https://geocoding-api.open-meteo.com/v1/search?name="
                + encode(city) + "&count=1&language=es&format=json");
        JSONArray results = geocoding.optJSONArray("results");
        if (results == null || results.length() == 0) return null;
        JSONObject place = results.getJSONObject(0);
        String name = place.optString("name", city);
        String admin = place.optString("admin1", "");
        String country = place.optString("country", "");
        String location = name;
        if (!admin.isEmpty() && !admin.equalsIgnoreCase(name)) location += ", " + admin;
        if (!country.isEmpty()) location += ", " + country;

        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + place.getDouble("latitude")
                + "&longitude=" + place.getDouble("longitude")
                + "&daily=weather_code,temperature_2m_max,temperature_2m_min,relative_humidity_2m_max"
                + "&temperature_unit=fahrenheit&timeformat=unixtime&timezone=auto";
        JSONObject daily = request(url).getJSONObject("daily");
        JSONArray times = daily.getJSONArray("time");
        JSONArray highs = daily.getJSONArray("temperature_2m_max");
        JSONArray lows = daily.getJSONArray("temperature_2m_min");
        JSONArray humidity = daily.optJSONArray("relative_humidity_2m_max");
        JSONArray codes = daily.getJSONArray("weather_code");
        List<Weather> days = new ArrayList<>();
        for (int i = 0; i < times.length(); i++) {
            String[] condition = condition(codes.getInt(i));
            days.add(new Weather(times.getLong(i), lows.getDouble(i), highs.getDouble(i),
                    humidity == null ? 65 : humidity.optDouble(i, 65), condition[0], condition[1]));
        }
        return new Forecast(location, days);
    }

    private Forecast parseOpenWeather(JSONObject response, String city) throws Exception {
        JSONArray list = response.getJSONArray("list");
        List<Weather> days = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject temp = item.getJSONObject("temp");
            JSONObject condition = item.getJSONArray("weather").getJSONObject(0);
            days.add(new Weather(item.getLong("dt"), temp.getDouble("min"), temp.getDouble("max"),
                    item.getDouble("humidity"), condition.getString("description"), condition.getString("icon")));
        }
        return new Forecast(city, days);
    }

    private JSONObject request(String address) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000);
        try {
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) throw new IllegalStateException("HTTP " + status);
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) body.append(line);
            }
            return new JSONObject(body.toString());
        } finally { connection.disconnect(); }
    }

    private String encode(String value) throws Exception { return URLEncoder.encode(value, "UTF-8"); }

    private String[] condition(int code) {
        switch (code) {
            case 0: return new String[]{"Cielo despejado", "01d"};
            case 1: return new String[]{"Mayormente despejado", "02d"};
            case 2: return new String[]{"Parcialmente nublado", "03d"};
            case 3: return new String[]{"Nublado", "04d"};
            case 45: case 48: return new String[]{"Niebla", "50d"};
            case 51: case 53: case 55: return new String[]{"Llovizna", "09d"};
            case 56: case 57: return new String[]{"Llovizna helada", "09d"};
            case 61: return new String[]{"Lluvia ligera", "10d"};
            case 63: return new String[]{"Lluvia moderada", "10d"};
            case 65: return new String[]{"Lluvia fuerte", "10d"};
            case 66: case 67: return new String[]{"Lluvia helada", "13d"};
            case 71: case 73: case 75: case 77: return new String[]{"Nevada", "13d"};
            case 80: case 81: case 82: return new String[]{"Chubascos de lluvia", "09d"};
            case 85: case 86: return new String[]{"Chubascos de nieve", "13d"};
            case 95: return new String[]{"Tormenta eléctrica", "11d"};
            case 96: case 99: return new String[]{"Tormenta eléctrica con granizo", "11d"};
            default: return new String[]{"Lluvia moderada", "10d"};
        }
    }
}
