package controllers;

import play.libs.F;
import play.mvc.*;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Flo on 24.04.2015.
 */
@With(AuthenticationAction.class)
public class AbstractAuthenticatedController extends Controller {


    public static Result whoop() throws IOException {
        System.out.println("-------- *,-,' -----------------------");
        return ok("alles gut!");
    }

}
