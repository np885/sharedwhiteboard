package controllers.whiteboards;

import controllers.common.Paths;
import controllers.common.dto.LinkedDTO;
import controllers.common.dto.XHref;
import controllers.common.mediatypes.ConsumesJSON;
import controllers.common.security.AuthRequired;
import controllers.users.dto.NewUserWriteDTO;
import controllers.users.routes;
import controllers.whiteboards.dto.NewWhiteboardWriteDTO;
import controllers.whiteboards.dto.WhiteboardCollectionReadDTO;
import controllers.whiteboards.dto.WhiteboardMapper;
import model.AlreadyExistsException;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;
import model.whiteboards.repositories.WhiteboardRepo;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

/**
 * Created by Flo on 06.05.2015.
 */
public class WhiteboardCollectionController extends Controller {

    //TODO doc
    @AuthRequired
    public static Result getWhiteboardCollection() {
        //TODO test
        List<Whiteboard> whiteboards = WhiteboardRepo.findAll();
        System.out.println(whiteboards);
        WhiteboardCollectionReadDTO collectionDto = new WhiteboardCollectionReadDTO();

        for (Whiteboard wb : whiteboards) {
            collectionDto.getBoards().add(WhiteboardMapper.mapEntityToReadDTO(wb));
        }

        addCreateWhiteboardXHref(collectionDto);

        return ok(Json.toJson(collectionDto));
    }


    //TODO doc
    @ConsumesJSON
    @AuthRequired
    public static Result createNewWhiteboard() {
        //TODO impl
        //TODO test
        NewWhiteboardWriteDTO newWhiteboardWriteDTO;
        try {
            newWhiteboardWriteDTO = Json.fromJson(request().body().asJson(), NewWhiteboardWriteDTO.class);
        } catch (Exception e) {
            return badRequest("Could not parse your json values:\n " + e.getCause().getMessage());
        }

        Whiteboard wb = WhiteboardMapper.mapFromNewWriteDTO(newWhiteboardWriteDTO);
        wb.setOwner((User) ctx().args.get("currentuser"));

        try {
            WhiteboardRepo.createWhiteboard(wb);
        } catch (AlreadyExistsException e) {
            return status(422, e.getMessage());
        }

        response().setHeader(
                Http.HeaderNames.LOCATION,
                Paths.forWhiteboard(wb));

        return created();
    }

    private static void addCreateWhiteboardXHref(LinkedDTO dto) {
        NewWhiteboardWriteDTO whiteboardWriteTemplate = new NewWhiteboardWriteDTO();

        whiteboardWriteTemplate.setName("My tiny Whiteboard.");

        dto.getActions().add (
                new XHref(
                        "create",
                        Paths.WHITEBOARDS_FULL,
                        XHref.POST,
                        whiteboardWriteTemplate)
        );
    }
}
