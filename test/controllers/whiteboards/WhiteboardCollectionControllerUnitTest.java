package controllers.whiteboards;

import controllers.util.AbstractControllerTest;
import model.AlreadyExistsException;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;
import model.whiteboards.repositories.WhiteboardRepo;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * test scenario: Create whiteboard and find it in
 */
public class WhiteboardCollectionControllerUnitTest extends AbstractControllerTest {

    @Test
    public void testOwnerIsCurrentUserOnCreate() throws AlreadyExistsException {
        final TestDataUtil tdu = new TestDataUtil();

        //mock request:
        mockContextWithJsonBody("{\"name\" : \"My Whiteboard #42\"}");

        //mock whiteboard repo:
        WhiteboardRepo wbRepoMock = mock(WhiteboardRepo.class);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                tdu.assertEntity((Whiteboard) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(wbRepoMock).createWhiteboard(any(Whiteboard.class));
        WhiteboardCollectionController.setRequired(wbRepoMock);

        tdu.prepCtx();
        Result result = WhiteboardCollectionController.createNewWhiteboard();
    }

}

class TestDataUtil {
    private User currentUser;

    public TestDataUtil() {
        currentUser = new User();
        currentUser.setUsername("Bill Yard");
    }


    public void prepCtx() {
        Http.Context.current().args.put("currentuser", currentUser);
    }

    public void assertEntity(Whiteboard entity) {
        Assert.assertEquals(currentUser, entity.getOwner());
    }

}
