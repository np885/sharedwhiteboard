package actors;

import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardActor extends UntypedActor {

    private String boardName;
    private List<WebSocketConnection> socketConnections = new ArrayList<>();

    @Override
    public void onReceive(Object message) throws Exception {

    }

}
