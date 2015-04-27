package controllers.common.mediatypes;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Arrays;

/**
 * used to send 415 "unsupported media type" response, if the content-type header of the request
 * is not "text/json" or "application/json"
 */
public class JSONonlyAction extends Action.Simple {
    private static final F.Promise<Result> unsopportedMediaType = F.Promise.pure(
            (Result) status(415, "The only supported content type is application/json"));

    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        String[] contentTypeHeader = context.request().headers().get("Content-Type");
        contentTypeHeader = (contentTypeHeader == null || contentTypeHeader.length == 0) ? null: contentTypeHeader[0].split(";");
        System.out.println(Arrays.toString(contentTypeHeader));
        if (contentTypeHeader == null || contentTypeHeader.length == 0 ||
                (!contentTypeHeader[0].equalsIgnoreCase("text/json") && !contentTypeHeader[0].equalsIgnoreCase("application/json"))) {
            return unsopportedMediaType;
        }
        return delegate.call(context);
    }
}
