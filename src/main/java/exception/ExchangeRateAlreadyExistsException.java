package exception;

public class ExchangeRateAlreadyExistsException extends RuntimeException {
    public ExchangeRateAlreadyExistsException(String codeBaseCurrency, String codeTargetCurrency) {
        super("Exchange rate is already exists with id base currency with code: " + codeBaseCurrency + " and target currency with code: " + codeTargetCurrency);
    }
}
