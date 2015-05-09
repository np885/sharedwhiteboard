package controllers.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.api.mvc.RequestHeader;
import play.libs.Json;
import play.mvc.Http;
import play.test.FakeApplication;
import play.test.Helpers;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class AbstractControllerTest {

    protected RequestHeader headerMock;
    protected Http.Request rqustMock;

    /**
     * see <a href=http://stackoverflow.com/questions/20206270/play-framework-2-2-1-create-http-context-for-tests>
     *     stackoverflow</a>
     *
     * @return mockRequest
     */
    protected void mockContextWithJsonBody(String body) {
        mockContext(true, body);
    }
    protected void mockContextWithoutBody() {
        mockContext(false, null);
    }

    private void mockContext(boolean withBody, String body) {
        Map<String, String> flashData = Collections.emptyMap();
        Map<String, Object> argData = Collections.emptyMap();
        Long id = 2L;

        headerMock = mock(RequestHeader.class);
        rqustMock = mock(Http.Request.class);

        if (withBody) {
            Http.RequestBody bodyMock = mock(Http.RequestBody.class);
            when(bodyMock.asJson()).thenReturn(Json.parse(body));
            when(rqustMock.body()).thenReturn(bodyMock);
        }

        Http.Context context = new Http.Context(id, headerMock, rqustMock, flashData, flashData, argData);
        Http.Context.current.set(context);
    }
}
