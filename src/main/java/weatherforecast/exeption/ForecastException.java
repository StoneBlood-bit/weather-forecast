package weatherforecast.exeption;

public class ForecastException extends RuntimeException {
    public ForecastException(String message, Throwable cause) {
        super(message, cause);
    }
}
