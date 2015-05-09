package controllers.whiteboards;

import model.AlreadyExistsException;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.mockito.Mockito.*;

/**
 * Testing behaviour of Controller on Whiteboard Creation.
 * <br>
 * Under Test: {@link WhiteboardCollectionController#createNewWhiteboard()}
 */
public class WhiteboardCollectionControllerCreateWhiteboardUnitTest extends AbstractWhiteboardCollectionControllerUnitTest {

    /**
     * Happy Day: Testing plain data would be persisted & correct Response is build by controller.
     *
     * @throws AlreadyExistsException unexpected!
     */
    @Test
    public void testWhiteboardCreation() throws AlreadyExistsException {
        final WhiteboardCreationTestDataUtil tdu = new WhiteboardCreationTestDataUtil();

        //mock request:
        mockContextWithJsonBody(tdu.getTestEntityASJSON());

        //mock whiteboard repo:
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                Whiteboard wb = (Whiteboard) invocationOnMock.getArguments()[0];
                tdu.mockPersistingEntity(wb);
                tdu.assertEntity(wb);
                return null;
            }
        }).when(wbRepoMock).createWhiteboard(any(Whiteboard.class));

        Result result = WhiteboardCollectionController.createNewWhiteboard();
        tdu.assertResult(result);
    }


    /**
     * Testing that the controller calls persisting layer with the current user set as owner of the whiteboard.
     * @throws AlreadyExistsException unexpected
     */
    @Test
    public void testOwnerIsCurrentUserOnCreate() throws AlreadyExistsException {
        final CurrentUserTestDataUtil tdu = new CurrentUserTestDataUtil();

        //mock request:
        mockContextWithJsonBody("{\"name\" : \"My Whiteboard #42\"}");

        //mock whiteboard repo:
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                tdu.assertEntity((Whiteboard) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(wbRepoMock).createWhiteboard(any(Whiteboard.class));

        tdu.prepCtx();
        WhiteboardCollectionController.createNewWhiteboard();
    }


    /**
     * Testing empty whiteboard names will fail.
     */
    @Test
    public void testInvalidEntityEmptyName() {
        mockContextWithJsonBody("{\"name\": \"\"}");
        Result result = WhiteboardCollectionController.createNewWhiteboard();

        Assert.assertEquals(400, Helpers.status(result));
    }

    /**
     * Testing controllers behaviour on already existing whiteboards.
     *
     * @throws AlreadyExistsException unexpected (should be catched by controller)
     */
    @Test
    public void testStatusIfWhiteboardAlreadyExists() throws AlreadyExistsException {
        mockContextWithJsonBody("{\"name\": \"asdfasdf\"}");
        doThrow(AlreadyExistsException.class).when(wbRepoMock).createWhiteboard(any(Whiteboard.class));

        Result result = WhiteboardCollectionController.createNewWhiteboard();

        Assert.assertEquals("wrong statuscode", 422, Helpers.status(result));
    }
}

class CurrentUserTestDataUtil {
    private User currentUser;

    public CurrentUserTestDataUtil() {
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

class WhiteboardCreationTestDataUtil {
    private static final String NAME_WHITEBOARD = "Everything on board #4711!";
    public static final long ID_WHITEBOARD = 47910234234L;

    public String getTestEntityASJSON() {
        return String.format("{\"name\":\"%s\"}", NAME_WHITEBOARD);
    }

    public void assertEntity(Whiteboard entity) {
        Assert.assertEquals("Whiteboard Name", NAME_WHITEBOARD, entity.getName());
    }

    public void mockPersistingEntity(Whiteboard wb) {
        wb.setId(ID_WHITEBOARD);
    }

    public void assertResult(Result result) {
        Assert.assertEquals("wrong status code", 201, Helpers.status(result));

        String locationHeader = Http.Context.current().response().getHeaders().get(Http.HeaderNames.LOCATION);
        Assert.assertNotNull("location header null", locationHeader);
        Assert.assertTrue(
                "location header check for EntityID",
                locationHeader.endsWith(ID_WHITEBOARD + "")
        );
    }
}
