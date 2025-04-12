package ro.unibuc.hello.exception;

public class TooManyEntriesException extends  RuntimeException{
    public TooManyEntriesException(String userId){
        super("The user " + userId + " has reached their short url creation limit");
    }
}
