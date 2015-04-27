package controllers.users;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.common.mediatypes.ConsumesJSON;
import controllers.common.security.AuthRequired;
import controllers.users.dto.NewUserWriteDTO;
import play.libs.Json;
import play.mvc.*;

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
    /*public, NO AUTHENTICATION needed!*/
    public static Result registerUser() {
        try {
            NewUserWriteDTO newUserWriteDTO = Json.fromJson(request().body().asJson(), NewUserWriteDTO.class);
            System.out.println(newUserWriteDTO.toString());
        } catch (Exception e) {
            return badRequest("Could not parse your json values:\n " + e.getCause().getMessage());
        }

        response().setHeader(
                Http.HeaderNames.LOCATION,
                routes.UserCollectionController.getAllUsers().absoluteURL(request()));
        return created();
    }



}
