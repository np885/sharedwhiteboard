package actors.events.socket.draw;

import actors.events.SocketEvent;

public abstract class DrawEvent extends SocketEvent {
    /* common representation for the client:
        {
            eventtype : '',
            boardElementId : 4711,
            ...specific attributes eg. x,y,width,height and so on
        }
     */


    private int boardElementId;

    public int getBoardElementId() {
        return boardElementId;
    }

    public void setBoardElementId(int boardElementId) {
        this.boardElementId = boardElementId;
    }
}
