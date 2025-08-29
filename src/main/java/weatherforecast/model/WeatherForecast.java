package weatherforecast.model;

import lombok.Data;

@Data
public class WeatherForecast {
    private String city;
    private String date;
    private double minTempC;
    private double maxTempC;
    private int humidity;
    private double windKph;
    private String windDir;
}
