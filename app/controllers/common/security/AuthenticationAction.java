package controllers.common.security;

import model.user.entities.User;
import model.user.repositories.UserRepo;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import sun.misc.BASE64Decoder;

import java.util.Map;

/**
 * Created by Flo on 24.04.2015.
 *
 *
 *
 */
public class AuthenticationAction extends Action.Simple {
    private static final Result unauthorizedResult = (Result) unauthorized("");
    private static final F.Promise<Result> unauthorizedResultPromise = F.Promise.pure(unauthorizedResult);

    @Override
    public F.Promise<Result> call(final Http.Context context) throws Throwable {
        Map<String, String[]> headers = context.request().headers();
        String[] authHeader = headers.get("Authorization");
        authHeader = (authHeader == null || authHeader.length == 0) ? null : authHeader[0].split(" ");
        /* now authHeader[0] contains the authentication method (expected to be "basic")
         * and authHeader[1] should contain the base64 hash!       */
        if (authHeader == null || ! authHeader[0].equalsIgnoreCase("basic")) {
            addAuthMethodHeader(context.response());
            return unauthorizedResultPromise;
        }

        BASE64Decoder decoder = new BASE64Decoder();
        String decoded = new String(decoder.decodeBuffer(authHeader[1]));

        String[] splittedDecode = decoded.split(":");
        final String username = splittedDecode[0];
        final String password = splittedDecode[1];

        User requestingUser = UserRepo.getUserForUsername(username);

        if (requestingUser == null || !requestingUser.getPassword().equals(password)) {
            //username not found or wrong password ^
            addAuthMethodHeader(context.response());
            return unauthorizedResultPromise;
        }

        context.args.put("currentuser", requestingUser);
        return delegate.call(context);
    }

    private void addAuthMethodHeader(Http.Response response) {
        response.setHeader(Http.HeaderNames.WWW_AUTHENTICATE, "basic");
    }

}
