package controllers.whiteboards.dto;

import controllers.common.dto.XHref;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardMapperUnitTest {


    @Test
    public void testMapFullEntityToDTO() {
        TestDataUtil tdu = new TestDataUtil();
        Whiteboard fullWhiteboard = tdu.createFullWhiteboard();
        WhiteboardReadDTO dto = WhiteboardMapper.mapEntityToReadDTO(fullWhiteboard);
        tdu.assertDto(dto);
    }

    @Test
    public void testMapDtoToEntity() {
        TestDataUtil tdu = new TestDataUtil();
        Whiteboard whiteboard = WhiteboardMapper.mapFromNewWriteDTO(tdu.createNewWBWrtieDTO());
        tdu.assertEntity(whiteboard);
    }


}

class TestDataUtil {
    public static final String WHITEBOARD_NAME = "My Name !\"§$%&/()=\\";
    public static final String OWNER_NAME = "Pit Schnass";
    public static final String COLLAB1_NAME = "Brad Hard";
    public static final String COLLAB2_NAME = "André Seite";

    @Test
    public Whiteboard createFullWhiteboard() {
        Whiteboard fullWhiteboard = new Whiteboard();
        fullWhiteboard.setName(WHITEBOARD_NAME);

        User owner = new User();
        owner.setUsername(OWNER_NAME);

        User collab1 = new User();
        collab1.setUsername(COLLAB1_NAME);

        User collab2 = new User();
        collab2.setUsername(COLLAB2_NAME);

        fullWhiteboard.setOwner(owner);
        fullWhiteboard.getCollaborators().add(owner);
        fullWhiteboard.getCollaborators().add(collab1);
        fullWhiteboard.getCollaborators().add(collab2);
        return fullWhiteboard;
    }

    public void assertDto(WhiteboardReadDTO dto) {
        Assert.assertEquals(WHITEBOARD_NAME, dto.getName());
        Assert.assertEquals(OWNER_NAME, ((WhiteboardMapper.UserDescription) dto.getOwner().getDescription()).getUsername());

        List<String> collaboratorNames = new ArrayList<>();
        for (XHref ref : dto.getCollaborators()) {
            collaboratorNames.add(((WhiteboardMapper.UserDescription) ref.getDescription()).getUsername());
        }

        Assert.assertTrue("collaborator with name '" + COLLAB1_NAME + "' not mapped", collaboratorNames.contains(COLLAB1_NAME));
        Assert.assertTrue("collaborator with name '" + COLLAB2_NAME + "' not mapped", collaboratorNames.contains(COLLAB2_NAME));
    }

    public NewWhiteboardWriteDTO createNewWBWrtieDTO() {
        NewWhiteboardWriteDTO dto = new NewWhiteboardWriteDTO();
        dto.setName(WHITEBOARD_NAME);
        return dto;
    }

    public void assertEntity(Whiteboard entity) {
        Assert.assertEquals(WHITEBOARD_NAME, entity.getName());
    }

}