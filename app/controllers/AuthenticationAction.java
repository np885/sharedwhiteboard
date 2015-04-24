package controllers;

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
 */
public class AuthenticationAction extends Action.Simple {
    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        String str = "";
        Map<String, String[]> headers = context.request().headers();
        String[] authHeader = headers.get("Authorization")[0].split(" ");
        if (authHeader == null || ! authHeader[0].equalsIgnoreCase("basic")) {
            //todo: fail request 403
            unauthorized();
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        String decoded = new String(decoder.decodeBuffer(authHeader[1]));

        String[] splittedDecode = decoded.split(":");
        String username = splittedDecode[0];
        String password = splittedDecode[1];

        if (username.equalsIgnoreCase("hans")) {
            return delegate.call(context);
        }

        Result r = unauthorized("Alles mist!");
        return  F.Promise.pure(r);
    }
}
