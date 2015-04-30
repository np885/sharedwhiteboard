package controllers.users.dto;

import model.user.entities.User;

/**
 * Created by Flo on 30.04.2015.
 */
public class UserMapper {

    public static User mapFromNewUserDTO(NewUserWriteDTO dto) {
        User mappedUser = new User();
        mappedUser.setUsername(dto.getUsername());
        mappedUser.setPassword(dto.getPassword());
        return mappedUser;
    }

}
