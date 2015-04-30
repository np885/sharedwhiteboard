package controllers.users;

import controllers.common.mediatypes.ConsumesJSON;
import controllers.common.security.AuthRequired;
import controllers.users.dto.NewUserWriteDTO;
import controllers.users.dto.UserMapper;
import model.user.entities.User;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

/**
 * Created by Flo on 26.04.2015.
 */
public class UserManagementController extends Controller {

    @AuthRequired
    public static Result checkLoginCredentials() {
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
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

        User userToSave = UserMapper.mapFromNewUserDTO(newUserWriteDTO);
//        try {
            if (getUserForUsername(userToSave.getUsername()) != null) {
                return status(422, "User already exists!");
            }
            JPA.em().persist(userToSave);
//            userToSave.save();<-- funktionierjt noch nicht...
//        } catch (UserAlreadyExistsException e) {
//            return status(422, "User already exists!");
//        }

        response().setHeader(
                Http.HeaderNames.LOCATION,
                routes.UserCollectionController.getAllUsers().absoluteURL(request()));
        return created();
    }

    private static boolean validateUserDTO(NewUserWriteDTO dto) {
        return dto != null
                && dto.getUsername() != null && !dto.getUsername().trim().isEmpty()
                && dto.getUsername() != null && !dto.getUsername().trim().isEmpty();
    }

    private static User getUserForUsername(String searchedUsername) {
        //TODO das ist hier voellig deplatziert, aber in andere klassen auslagern hat noch nicht funktioniert, siehe User
        List<User> users = JPA.em().createQuery("SELECT u FROM User u WHERE u.username=:username")
                .setParameter("username", searchedUsername)
                .getResultList();
        if (users.size() > 0) {
            Logger.warn("several Users found for username " + searchedUsername);
        }

        return (users.size() > 0) ? users.get(0) : null;
    }
}
