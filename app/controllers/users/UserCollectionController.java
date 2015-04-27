package controllers.users;

import controllers.users.dto.UserReadDTO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Flo on 27.04.2015.
 */
public class UserCollectionController extends Controller {

    public static Result getAllUsers() {
        List<UserReadDTO> allUsers = Arrays.asList(new UserReadDTO[]{new UserReadDTO("hans1"), new UserReadDTO("hans2"), new UserReadDTO("hans3")});

        return ok(Json.toJson(allUsers));
    }
}
