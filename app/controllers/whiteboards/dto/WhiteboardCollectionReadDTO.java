package controllers.whiteboards.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import controllers.common.Paths;
import controllers.common.dto.LinkedDTO;
import controllers.common.dto.XHref;
import play.core.Router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Flo on 06.05.2015.
 */
public class WhiteboardCollectionReadDTO implements LinkedDTO {
    private List<WhiteboardReadDTO> boards = new ArrayList<>();

    private List<XHref> actions = new ArrayList<>();

    public List<WhiteboardReadDTO> getBoards() {
        return boards;
    }

    @Override
    public List<XHref> getActions() {
        return actions;
    }

}


