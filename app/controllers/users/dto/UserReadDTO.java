package controllers.users.dto;

public class UserReadDTO {
    private long id;


    private String username;

    public UserReadDTO(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}