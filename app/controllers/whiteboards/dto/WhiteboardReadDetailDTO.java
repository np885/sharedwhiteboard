package controllers.whiteboards.dto;

import controllers.common.dto.LinkedDTO;
import controllers.common.dto.XHref;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class WhiteboardReadDetailDTO extends WhiteboardReadDTO implements LinkedDTO {
    private List<XHref> actions = new ArrayList<>();

    @Override
    public List<XHref> getActions() {
        return actions;
    }
}
