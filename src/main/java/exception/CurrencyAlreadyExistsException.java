package exception;

public class CurrencyAlreadyExistsException extends RuntimeException {
    public CurrencyAlreadyExistsException(String code) {
        super("Currency with code: " + code + " is already exists");
    }
}
