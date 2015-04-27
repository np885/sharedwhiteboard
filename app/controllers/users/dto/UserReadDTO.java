package controllers.users.dto;

public class UserReadDTO {
    private String username;

    public UserReadDTO(String userName) {
        this.username = userName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}