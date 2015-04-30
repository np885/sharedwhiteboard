package model.user;

/**
 * Created by Flo on 30.04.2015.
 */
public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
