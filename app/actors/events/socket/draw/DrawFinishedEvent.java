package actors.events.socket.draw;

import java.util.Date;

/**
 * Created by niclas on 06.06.15.
 */
public class DrawFinishedEvent extends DrawEvent{

    private String drawType;

    private Date logDate;

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getDrawType() {
        return drawType;
    }

    public void setDrawType(String drawType) {
        this.drawType = drawType;
    }

    @Override
    public String getEventType() {
        return "DrawFinishEvent";
    }
}
