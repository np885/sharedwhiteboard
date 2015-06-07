package controllers.whiteboards;


import com.fasterxml.jackson.databind.JsonNode;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;
import org.junit.Assert;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Testing behaviour of Controller on GET of collection
 * <br>
 * Under Test: {@link WhiteboardCollectionController#getWhiteboardCollection()}
 */
public class WhiteboardCollectionControllerGetCollectionUnitTest extends AbstractWhiteboardCollectionControllerUnitTest {

    /**
     * Happy Day: Testing all Data of the Repo is parsed correctly.
     */
    @Test
    public void testCollectionGet() {
        TestDataUtil tdu = new TestDataUtil();
        mockContextWithoutBody();

        when(wbRepoMock.findAll()).thenReturn(tdu.createEntityList());
        
        Result result = WhiteboardCollectionController.getWhiteboardCollection();
        tdu.assertResult(result);
    }

    /**
     * Testing behaviour of controllor on empty collection
     */
    @Test
    public void testEmptyCollection() {
        mockContextWithoutBody();

        when(wbRepoMock.findAll()).thenReturn(new ArrayList<Whiteboard>());

        Result result = WhiteboardCollectionController.getWhiteboardCollection();

        Assert.assertEquals("statuscode", 200, Helpers.status(result));
        JsonNode body = Json.parse(Helpers.contentAsString(result));
        Assert.assertEquals("", "[]", body.get("boards").toString());

    }

}

class TestDataUtil {
    private static final String NAME_1 = "Whity the board, with collabs";
    private static final String NAME_2 = "No collabs today!";
    private static final long ID_1 = 5678919L;
    private static final long ID_2 = Long.MAX_VALUE;
    private static final User USER_A, USER_B, USER_C;

    static {
        USER_A = new User();
        USER_A.setUsername("Brad Hard");
        USER_A.setId(1L);
        USER_B = new User();
        USER_B.setUsername("Alf A. Romeo");
        USER_B.setId(2L);

        USER_C = new User();
        USER_C.setUsername("Arno Nühm");
        USER_C.setId(3L);

    }

    private Whiteboard wb1;
    private Whiteboard wb2;


    public void assertResult(Result result) {
        JsonNode body = Json.parse(Helpers.contentAsString(result));
        JsonNode board1 = null, board2 = null;
        for (JsonNode boardNode : body.get("boards")) {
            if (boardNode.get("id").asLong() == ID_1) {
                board1 = boardNode;
            } else if (boardNode.get("id").asLong() == ID_2) {
                board2 = boardNode;
            }
        }

        Assert.assertNotNull("whiteboard 1 not found", board1);
        Assert.assertNotNull("whiteboard 2 not found", board2);

        Assert.assertEquals("boardname 1", NAME_1, board1.get("name").asText());
        Assert.assertEquals("boardname 2", NAME_2, board2.get("name").asText());

        Assert.assertEquals(
                "board 1 owner name",
                wb1.getOwner().getUsername(),
                board1.get("owner").get("description").get("username").asText());
        Assert.assertEquals(
                "board 2 owner name",
                wb2.getOwner().getUsername(),
                board2.get("owner").get("description").get("username").asText());

        Assert.assertEquals("board 2 collabs should be empty", "[]", board2.get("collaborators").toString());
        Assert.assertNotEquals("board 1 collabs should not be empty", "[]", board1.get("collaborators").toString());
    }

    public List<Whiteboard> createEntityList() {
        List<Whiteboard> list = new ArrayList<>();

        wb1 = new Whiteboard();
        wb1.setName(NAME_1);
        wb1.setId(ID_1);
        wb1.setOwner(USER_A);
        wb1.getCollaborators().add(USER_A);
        wb1.getCollaborators().add(USER_B);
        wb1.getCollaborators().add(USER_C);
        list.add(wb1);

        wb2 = new Whiteboard();
        wb2.setName(NAME_2);
        wb2.setId(ID_2);
        wb2.setOwner(USER_C);
        list.add(wb2);
        return list;
    }


}