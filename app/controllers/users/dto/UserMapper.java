package controllers.users.dto;

import controllers.common.security.HashUtil;
import model.user.entities.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Flo on 30.04.2015.
 */
public class UserMapper {

    public static User mapFromNewUserDTO(NewUserWriteDTO dto) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        User mappedUser = new User();
        mappedUser.setUsername(dto.getUsername().toLowerCase());
        mappedUser.setPassword(HashUtil.hashString(dto.getPassword()));
        return mappedUser;
    }

    public static UserReadDTO mapToReadDTO(User user) {
        UserReadDTO dto = new UserReadDTO(user.getId(), user.getUsername());
        return dto;
    }

    public static UserReadDTO mapToReadDTO(User user, Long joinedBoardId) {
        UserReadDTO dto = mapToReadDTO(user);
        dto.setCurrentlyJoinedBoardId(joinedBoardId);
        return dto;
    }



}
