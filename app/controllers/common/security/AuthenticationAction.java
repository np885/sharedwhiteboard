package controllers.common.security;

import model.user.entities.User;
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

        //TODO refactorn, sobald klar ist, wie DB-Abfragen ausgelagert werden koennen

        Boolean authenticated = JPA.withTransaction(new F.Function0<Boolean>() {

            @Override
            public Boolean apply() throws Throwable {
                User userForUsername = getUserForUsername(username);
                return userForUsername != null && userForUsername.getPassword().equals(password);
            }
        });

        if (authenticated) {
            return delegate.call(context);
        }
        return unauthorizedResult;
    }

    private static User getUserForUsername(String searchedUsername) {
        //TODO das ist hier voellig deplatziert, aber in andere klassen auslagern hat noch nicht funktioniert, siehe User
        List<User> users = JPA.em().createQuery("SELECT u FROM User u WHERE u.username=:username")
                .setParameter("username", searchedUsername)
                .getResultList();
        if (users.size() > 1) {
            Logger.warn("several Users found for username " + searchedUsername);
        }

        return (users.size() > 0) ? users.get(0) : null;
    }
}
