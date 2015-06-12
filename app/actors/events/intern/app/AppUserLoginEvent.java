package actors.events.intern.app;


import actors.list.ListSocketConnection;

public class AppUserLoginEvent extends AbstractAppUserEvent{
    private ListSocketConnection socketConnection;

    public ListSocketConnection getSocketConnection() {
        return socketConnection;
    }

    public void setSocketConnection(ListSocketConnection socketConnection) {
        this.socketConnection = socketConnection;
    }
}
