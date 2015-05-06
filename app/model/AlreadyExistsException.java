package model;

/**
 * Created by Flo on 30.04.2015.
 */
public class AlreadyExistsException extends Exception {

    public AlreadyExistsException() {
    }

    public AlreadyExistsException(String message) {
        super(message);
    }
}
