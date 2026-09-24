package fisei.uta.edu.ec.weatherviewerapp.domain.entities;

/** One daily forecast entry, independent of Android and network formats. */
public final class Weather {
    public final long timestamp;
    public final double minTemperature;
    public final double maxTemperature;
    public final double humidity;
    public final String description;
    public final String iconCode;

    public Weather(long timestamp, double minTemperature, double maxTemperature,
                   double humidity, String description, String iconCode) {
        this.timestamp = timestamp;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.humidity = humidity;
        this.description = description;
        this.iconCode = iconCode;
    }
}
