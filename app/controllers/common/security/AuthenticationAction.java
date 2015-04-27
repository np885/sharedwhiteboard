package controllers.common.security;

import org.h2.server.web.WebApp;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.SimpleResult;
import sun.misc.BASE64Decoder;

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
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        String str = "";
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
        String username = splittedDecode[0];
        String password = splittedDecode[1];

        //TODO DB-Abfrage
        if (username.equalsIgnoreCase("hans")) {
            return delegate.call(context);
        }

        return unauthorizedResult;
    }
}
