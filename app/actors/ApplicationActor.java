package actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashMap;
import java.util.Map;

public class ApplicationActor extends UntypedActor {

    /* maps <BoardName, BoardActor> */
    private Map<String, ActorRef> boardActors = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Exception {

    }


}
