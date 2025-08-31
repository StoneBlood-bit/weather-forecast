package weatherforecast.service;

import io.github.cdimascio.dotenv.Dotenv;
import weatherforecast.exeption.ForecastException;
import weatherforecast.model.WeatherForecast;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;

public class WeatherService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = "http://api.weatherapi.com/v1/forecast.json";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherForecast getForecast(String city) {
        return getForecastSafely(city);
    }

    private WeatherForecast getForecastSafely(String city) {
        try {
            String apiKey = dotenv.get("API_KEY");
            String url = URL + "?key=" + apiKey + "&q=" + city + "&days=2&aqi=no&alerts=no";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode forecastTomorrow = root.path("forecast").path("forecastday").get(1).path("day");

            WeatherForecast forecast = new WeatherForecast();
            forecast.setCity(city);

            forecast.setDate(safeGet(() -> root
                    .path("forecast").path("forecastday").get(1).path("date").asText(), "N/A"));
            forecast.setMaxTempC(safeGet(() -> forecastTomorrow
                    .path("maxtemp_c").asDouble(), 0.0));
            forecast.setMinTempC(safeGet(() -> forecastTomorrow
                    .path("mintemp_c").asDouble(), 0.0));
            forecast.setHumidity(safeGet(() -> forecastTomorrow
                    .path("avghumidity").asInt(), 0));
            forecast.setWindKph(safeGet(() -> forecastTomorrow
                    .path("maxwind_kph").asDouble(), 0.0));
            forecast.setWindDir(safeGet(() -> forecastTomorrow
                    .path("condition").path("text").asText(), "Not loaded")
            );

            return forecast;
        } catch (Exception e) {
            throw new ForecastException("Failed to fetch forecast for city: " + city, e);
        }
    }

    private <T> T safeGet(Supplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return fallback;
        }
    }
}
