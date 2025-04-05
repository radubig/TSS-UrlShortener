package ro.unibuc.hello.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public enum SameCredentials {
        USERNAME, EMAIL
    }

    public UserAlreadyExistsException(SameCredentials credentials) {
        super("User with the same " + credentials.toString().toLowerCase() + " already exists");
    }
}
