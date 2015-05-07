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
 * Producing Representations for Whiteboard Collection:
 * "{@value Paths#WHITEBOARDS_RELATIVE}"
 */
public class WhiteboardCollectionController extends Controller {

    /**
     * GET whiteboard collection
     *
     * @return ok with {@link WhiteboardCollectionReadDTO}
     */
    @AuthRequired
    public static Result getWhiteboardCollection() {
        //TODO test
        //fetch from db:
        List<Whiteboard> whiteboards = WhiteboardRepo.findAll();
        WhiteboardCollectionReadDTO collectionDto = new WhiteboardCollectionReadDTO();

        //map:
        for (Whiteboard wb : whiteboards) {
            collectionDto.getBoards().add(WhiteboardMapper.mapEntityToReadDTO(wb));
        }

        //response:
        addCreateWhiteboardXHref(collectionDto);

        return ok(Json.toJson(collectionDto));
    }


    /**
     * POST whiteboard item
     *
     * @return created with location link to Whiteboard; 422 if whiteboard name already exists.
     */
    @ConsumesJSON
    @AuthRequired
    public static Result createNewWhiteboard() {
        //TODO test
        //parse body:
        NewWhiteboardWriteDTO newWhiteboardWriteDTO;
        try {
            newWhiteboardWriteDTO = Json.fromJson(request().body().asJson(), NewWhiteboardWriteDTO.class);
        } catch (Exception e) {
            return badRequest("Could not parse your json values:\n " + e.getCause().getMessage());
        }

        //map: current authenticated user will be the owner.
        Whiteboard wb = WhiteboardMapper.mapFromNewWriteDTO(newWhiteboardWriteDTO);
        wb.setOwner((User) ctx().args.get("currentuser"));

        //persist:
        try {
            WhiteboardRepo.createWhiteboard(wb);
        } catch (AlreadyExistsException e) {
            return status(422, e.getMessage());
        }

        //response:
        response().setHeader(
                Http.HeaderNames.LOCATION,
                Paths.forWhiteboard(wb));

        return created();
    }

    /**
     * Adds a POST-{@link XHref} to the actions-list of dto.
     *
     * @param dto the dto to add the actionlink to.
     */
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
