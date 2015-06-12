package controllers.whiteboards;

import model.user.entities.User;
import play.mvc.WebSocket;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class SocketTicketSystem {

    private static class TicketInformation {
        private User user;
        private Calendar timestamp;
        private long boardId;

        //maps <key, value> of whatever:
        private Map<String, String> additionalInfos = new HashMap<>();
    }

    private static Map<String, TicketInformation> tickets = new HashMap<>();
    private static SecureRandom random = new SecureRandom();

    /**
     * @param user
     * @param properties
     * @return the ticketnumber
     */
    public static String createTicket(User user, Map<String, String> properties) {

        TicketInformation ticket = new TicketInformation();
        ticket.user = user;
        ticket.timestamp = Calendar.getInstance();
        if (properties != null) {
            ticket.additionalInfos.putAll(properties);
        }
        String ticketNumber = new BigInteger(130, random).toString(32);
        tickets.put(ticketNumber, ticket);

        return ticketNumber;
    }

    public boolean validate(String ticketNumber, Map<String, String> desiredProperties) {
        final TicketInformation requestedTicket = tickets.get(ticketNumber);
        if (requestedTicket == null) {
            return false;
        }

        for (String key : desiredProperties.keySet()) {
            if (!requestedTicket.additionalInfos.containsKey(key)) {
                return false;
            } else if (! desiredProperties.get(key).equals(requestedTicket.additionalInfos.get(key))) {
                return false;
            }
        }

        return true;
    }

    public User invalidate(String ticketNumber) {
        User user = tickets.get(ticketNumber).user;
        tickets.remove(ticketNumber);
        return user;
    }

}
