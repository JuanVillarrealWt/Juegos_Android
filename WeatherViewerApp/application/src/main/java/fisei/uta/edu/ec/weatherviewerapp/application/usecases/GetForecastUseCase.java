package fisei.uta.edu.ec.weatherviewerapp.application.usecases;

import fisei.uta.edu.ec.weatherviewerapp.application.contracts.repositories.WeatherRepository;
import fisei.uta.edu.ec.weatherviewerapp.domain.entities.Forecast;

public final class GetForecastUseCase {
    private final WeatherRepository repository;

    public GetForecastUseCase(WeatherRepository repository) {
        this.repository = repository;
    }

    public Forecast execute(String city) throws Exception {
        if (city == null || city.trim().isEmpty()) throw new IllegalArgumentException("City is required");
        return repository.getForecast(city.trim());
    }
}
