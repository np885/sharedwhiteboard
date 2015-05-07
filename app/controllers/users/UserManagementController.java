package controllers.users;

import controllers.common.Paths;
import controllers.common.mediatypes.ConsumesJSON;
import controllers.common.security.AuthRequired;
import controllers.users.dto.NewUserWriteDTO;
import controllers.users.dto.UserMapper;
import model.AlreadyExistsException;
import model.user.entities.User;
import model.user.repositories.UserRepo;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by Flo on 26.04.2015.
 */
public class UserManagementController extends Controller {

    @AuthRequired
    @Transactional
    public static Result checkLoginCredentials() {
        return ok();
    }

    @ConsumesJSON
    @Transactional
    /*public, NO AUTHENTICATION needed!*/
    public static Result registerUser() {
        //parse input:
        NewUserWriteDTO newUserWriteDTO;
        try {
            newUserWriteDTO = Json.fromJson(request().body().asJson(), NewUserWriteDTO.class);
        } catch (Exception e) {
            return badRequest("Could not parse your json values:\n " + e.getCause().getMessage());
        }

        //validate:
        if (!validateUserDTO(newUserWriteDTO)) {
            return badRequest("could not find a valid (not empty) username or password in your payload!");
        }

        //map:
        User userToSave = UserMapper.mapFromNewUserDTO(newUserWriteDTO);

        try {
            UserRepo.createNewUser(userToSave);
        } catch (AlreadyExistsException e) {
            //semantic error => "unproc. entity" status 422
            return status(422, "User already exists!");
        }

        //set location link header to user collection
        response().setHeader(
                Http.HeaderNames.LOCATION,
                Paths.USERS_FULL);

        return created();
    }

    /**
     * Validates that the dto has all necessary fields not empty.
     *
     * @param dto
     * @return true if valid
     */
    private static boolean validateUserDTO(NewUserWriteDTO dto) {
        return dto != null
                && dto.getUsername() != null && !dto.getUsername().trim().isEmpty()
                && dto.getUsername() != null && !dto.getUsername().trim().isEmpty();
    }


}
