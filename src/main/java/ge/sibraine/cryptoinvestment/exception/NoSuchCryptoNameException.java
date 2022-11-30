package ge.sibraine.cryptoinvestment.exception;

public class NoSuchCryptoNameException extends Exception {

    public NoSuchCryptoNameException() {
        super();
    }

    public NoSuchCryptoNameException(String message) {
        super(message);
    }

    public NoSuchCryptoNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
