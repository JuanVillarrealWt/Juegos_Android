package fisei.uta.edu.ec.weatherviewerapp.domain.entities;

import java.util.List;

public final class Forecast {
    public final String locationName;
    public final List<Weather> days;

    public Forecast(String locationName, List<Weather> days) {
        this.locationName = locationName;
        this.days = days;
    }
}
