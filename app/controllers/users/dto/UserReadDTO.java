package controllers.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class UserReadDTO {
    private long id;


    private String username;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Long currentlyJoinedBoardId;

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

    public Long getCurrentlyJoinedBoardId() {
        return currentlyJoinedBoardId;
    }

    public void setCurrentlyJoinedBoardId(Long currentlyJoinedBoardId) {
        this.currentlyJoinedBoardId = currentlyJoinedBoardId;
    }
}