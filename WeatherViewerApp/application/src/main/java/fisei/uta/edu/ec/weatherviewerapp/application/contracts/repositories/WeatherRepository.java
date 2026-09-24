package fisei.uta.edu.ec.weatherviewerapp.application.contracts.repositories;

import fisei.uta.edu.ec.weatherviewerapp.domain.entities.Forecast;

public interface WeatherRepository {
    Forecast getForecast(String city) throws Exception;
}
