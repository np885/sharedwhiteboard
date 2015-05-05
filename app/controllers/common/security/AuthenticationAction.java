package controllers.common.security;

import model.user.entities.User;
import model.user.repositories.UserRepo;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import sun.misc.BASE64Decoder;

import java.util.List;
import java.util.Map;

/**
 * Created by Flo on 24.04.2015.
 *
 *
 *
 */
public class AuthenticationAction extends Action.Simple {
    private static final F.Promise<Result> unauthorizedResult = F.Promise.pure((Result) unauthorized(""));

    @Override
    public F.Promise<Result> call(final Http.Context context) throws Throwable {
        Map<String, String[]> headers = context.request().headers();
        String[] authHeader = headers.get("Authorization");
        authHeader = (authHeader == null || authHeader.length == 0) ? null : authHeader[0].split(" ");
        /* now authHeader[0] contains the authentication method (expected to be "basic")
         * and authHeader[1] should contain the base64 hash!       */
        if (authHeader == null || ! authHeader[0].equalsIgnoreCase("basic")) {
            return unauthorizedResult;
        }

        BASE64Decoder decoder = new BASE64Decoder();
        String decoded = new String(decoder.decodeBuffer(authHeader[1]));

        String[] splittedDecode = decoded.split(":");
        final String username = splittedDecode[0];
        final String password = splittedDecode[1];

        User requestingUser = UserRepo.getUserForUsername(username);

        if (requestingUser == null || !requestingUser.getPassword().equals(password)) {
            //username not found or wrong password ^
            return unauthorizedResult;
        }
        return delegate.call(context);
    }

}
