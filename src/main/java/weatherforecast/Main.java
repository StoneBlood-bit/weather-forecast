package weatherforecast;

import weatherforecast.model.WeatherForecast;
import weatherforecast.service.WeatherService;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        WeatherService weatherService = new WeatherService();
        List<String> cities = List.of("Chisinau", "Madrid", "Kyiv", "Amsterdam");

        System.out.printf("%-12s | %-12s | %-8s | %-8s | %-8s | %-8s | %-15s%n",
                "City", "Date", "Min °C", "Max °C", "Humidity", "Wind kph", "Wind Dir");
        System.out.println("--------------------------------------------------------------------------------");

        for (String city: cities) {
            WeatherForecast forecast = weatherService.getForecast(city);
            System.out.printf("%-12s | %-12s | %-8.1f | %-8.1f | %-8d | %-8.1f | %-15s%n",
                    forecast.getCity(), forecast.getDate(), forecast.getMinTempC(),
                    forecast.getMaxTempC(), forecast.getHumidity(), forecast.getWindKph(),
                    forecast.getWindDir());
        }
    }
}
