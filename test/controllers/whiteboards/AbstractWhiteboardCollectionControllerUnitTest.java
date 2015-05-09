package controllers.whiteboards;


import controllers.util.AbstractControllerTest;
import model.whiteboards.repositories.WhiteboardRepo;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class AbstractWhiteboardCollectionControllerUnitTest extends AbstractControllerTest {
    protected WhiteboardRepo wbRepoMock;

    @Before
    public void setupMockComponents() {
        wbRepoMock = mock(WhiteboardRepo.class);
        WhiteboardCollectionController.setRequired(wbRepoMock);
    }

}
