package fisei.uta.edu.ec.weatherviewerapp;

import android.content.Context;
import fisei.uta.edu.ec.weatherviewerapp.application.usecases.GetForecastUseCase;
import fisei.uta.edu.ec.weatherviewerapp.infrastructure.repositories.HttpWeatherRepository;

/** Creates the concrete dependency graph at the Android application boundary. */
public final class WeatherCompositionRoot {
    private WeatherCompositionRoot() { }

    public static GetForecastUseCase createGetForecastUseCase(Context context) {
        HttpWeatherRepository repository = new HttpWeatherRepository(
                context.getString(R.string.api_key),
                context.getString(R.string.web_service_url));
        return new GetForecastUseCase(repository);
    }
}
