package id.orbion.ecommerce_app.common.error;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String message) {
        super(message);
    }

}
