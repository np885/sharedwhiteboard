package actors.events.socket.boardsessions;

public class BoardUserOpenSocketEvent extends AbstractBoardUserSocketEvent {

    @Override
    public String getEventType() {
        return "BoardUserOpenEvent";
    }


}
