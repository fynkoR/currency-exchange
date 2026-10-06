package exception;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException(String from, String to) {
        super("Exchange rate not found: " + from + "->" + to);
    }
}
