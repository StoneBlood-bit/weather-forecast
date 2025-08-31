package weatherforecast.service;

import io.github.cdimascio.dotenv.Dotenv;
import weatherforecast.exeption.ForecastException;
import weatherforecast.model.WeatherForecast;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = "http://api.weatherapi.com/v1/forecast.json";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherForecast getForecast(String city) {
        String apiKey = dotenv.get("API_KEY");
        try {
            String url = URL + "?key=" + apiKey + "&q=" + city + "&days=2&aqi=no&alerts=no";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());

            JsonNode forecastTomorrow = root.path("forecast").path("forecastday").get(1).path("day");

            WeatherForecast forecast = new WeatherForecast();
            forecast.setCity(city);
            forecast.setDate(root.path("forecast").path("forecastday").get(1).path("date").asText());
            forecast.setMaxTempC(forecastTomorrow.path("maxtemp_c").asDouble());
            forecast.setMinTempC(forecastTomorrow.path("mintemp_c").asDouble());
            forecast.setHumidity(forecastTomorrow.path("avghumidity").asInt());
            forecast.setWindKph(forecastTomorrow.path("maxwind_kph").asDouble());
            forecast.setWindDir(forecastTomorrow.path("condition").path("text").asText());
            return forecast;
        } catch (Exception e) {
            throw new ForecastException("Failed to fetch forecast for city: " + city);
        }
    }
}
