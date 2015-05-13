package controllers.whiteboards;

import controllers.common.Paths;
import controllers.common.dto.XHref;
import controllers.common.security.AuthRequired;
import controllers.whiteboards.dto.WhiteboardMapper;
import controllers.whiteboards.dto.WhiteboardReadDetailDTO;
import model.whiteboards.entities.Whiteboard;
import model.whiteboards.repositories.WhiteboardRepo;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 */
public class WhiteboardItemController extends Controller {
    private static WhiteboardRepo whiteboardRepo = new WhiteboardRepo();

    //todo doc
    @AuthRequired
    public static Result get(long id) {
        //todo test

        //fetch from db:
        Whiteboard whiteboard = whiteboardRepo.getWhiteboardForId(id);
        if (whiteboard == null) {
            return notFound("No whiteboard fond for the id " + id);
        }
        //map:
        WhiteboardReadDetailDTO dto = WhiteboardMapper.mapEntityToReadDetailDTO(whiteboard);
        dto.getActions().add(new XHref("collection", Paths.WHITEBOARDS_FULL, null, null));
        //response:

        return ok(Json.toJson(dto));
    }



    /**
     * "inject" (overwrite) required components.
     *
     * @param whiteboardRepo
     */
    public static void setRequired(WhiteboardRepo whiteboardRepo) {
        WhiteboardItemController.whiteboardRepo = whiteboardRepo;
    }
}
