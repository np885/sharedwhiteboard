package controllers.users.dto;

/**
 * Created by Flo on 27.04.2015.
 */
public class NewUserWriteDTO {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "NewUserWriteDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
