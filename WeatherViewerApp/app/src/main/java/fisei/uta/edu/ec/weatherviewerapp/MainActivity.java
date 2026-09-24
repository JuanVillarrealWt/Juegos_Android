package fisei.uta.edu.ec.weatherviewerapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import fisei.uta.edu.ec.weatherviewerapp.application.usecases.GetForecastUseCase;
import fisei.uta.edu.ec.weatherviewerapp.domain.entities.Forecast;
import fisei.uta.edu.ec.weatherviewerapp.domain.entities.Weather;
import fisei.uta.edu.ec.weatherviewerapp.presentation.WeatherArrayAdapter;
import java.util.ArrayList;
import java.util.List;

/** Android screen: collects input, runs the use case off the UI thread and renders its result. */
public class MainActivity extends AppCompatActivity {
    private final List<Weather> days = new ArrayList<>();
    private WeatherArrayAdapter adapter;
    private ListView forecastList;
    private GetForecastUseCase getForecast;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getForecast = WeatherCompositionRoot.createGetForecastUseCase(this);
        forecastList = findViewById(R.id.weatherListView);
        adapter = new WeatherArrayAdapter(this, days);
        forecastList.setAdapter(adapter);

        EditText cityInput = findViewById(R.id.locationEditText);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            String city = cityInput.getText().toString().trim();
            if (city.isEmpty()) {
                Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.hint_text, Snackbar.LENGTH_LONG).show();
                return;
            }
            hideKeyboard(cityInput);
            new FetchForecastTask().execute(city);
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private final class FetchForecastTask extends AsyncTask<String, Void, Forecast> {
        private Exception failure;
        @Override protected Forecast doInBackground(String... cities) {
            try { return getForecast.execute(cities[0]); }
            catch (Exception e) { failure = e; return null; }
        }
        @Override protected void onPostExecute(Forecast result) {
            if (result == null) {
                days.clear();
                adapter.notifyDataSetChanged();
                boolean cityNotFound = failure == null || failure instanceof IllegalArgumentException;
                if (getSupportActionBar() != null) getSupportActionBar().setSubtitle(
                        cityNotFound ? getString(R.string.city_not_found) : "");
                int message = cityNotFound ? R.string.city_not_found : R.string.connect_error;
                Snackbar.make(findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG).show();
                return;
            }
            days.clear();
            days.addAll(result.days);
            adapter.notifyDataSetChanged();
            forecastList.smoothScrollToPosition(0);
            if (getSupportActionBar() != null) getSupportActionBar().setSubtitle(result.locationName);
        }
    }
}
