package actors.serialization.boardstate;

import actors.serialization.AbstractSocketOutDTO;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class InitialBoardStateEventDTO extends AbstractSocketOutDTO {
    private List<Collab> colaborators = new ArrayList<>();

    @Override
    public String getEventType() {
        return "InitialBoardStateEvent";
    }

    public List<Collab> getColaborators() {
        return colaborators;
    }
}


