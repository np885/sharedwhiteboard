package controllers.users;

import actors.ApplicationActor;
import actors.board.BoardSocketInActor;
import actors.list.ListSocketInActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import controllers.common.Paths;
import controllers.common.mediatypes.ConsumesJSON;
import controllers.common.security.AuthRequired;
import controllers.users.dto.NewUserWriteDTO;
import controllers.users.dto.UserMapper;
import controllers.users.dto.UserReadDTO;
import controllers.whiteboards.SocketTicketSystem;
import model.AlreadyExistsException;
import model.user.entities.User;
import model.user.repositories.UserRepo;
import play.db.jpa.Transactional;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Flo on 26.04.2015.
 */
public class UserManagementController extends Controller {

    private static SocketTicketSystem ticketSystem = new SocketTicketSystem();


    @AuthRequired
    @Transactional
    public static Result checkLoginCredentials() {
        User currentuser = (User) ctx().args.get("currentuser");
        return ok(Json.toJson(UserMapper.mapToReadDTO(currentuser)));
    }

    @Transactional
    public static Result logout() {
        User currentuser = (User) ctx().args.get("currentuser");
        return ok();
    }

    @ConsumesJSON
    @Transactional
    /*public, NO AUTHENTICATION needed!*/
    public static Result registerUser() {
        //parse input:
        NewUserWriteDTO newUserWriteDTO;
        try {
            newUserWriteDTO = Json.fromJson(request().body().asJson(), NewUserWriteDTO.class);
        } catch (Exception e) {
            return badRequest("Could not parse your json values:\n " + e.getCause().getMessage());
        }

        //validate:
        if (!validateUserDTO(newUserWriteDTO)) {
            return badRequest("could not find a valid (not empty) username or password in your payload!");
        }

        //map:
        User userToSave = null;
        try {
            userToSave = UserMapper.mapFromNewUserDTO(newUserWriteDTO);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            //Hmm should never happen
            return badRequest("Something went wrong! Please contact the Admin!");
        }

        try {
            UserRepo.createNewUser(userToSave);
        } catch (AlreadyExistsException e) {
            //semantic error => "unproc. entity" status 422
            return status(422, "User already exists!");
        }

        //set location link header to user collection
        response().setHeader(
                Http.HeaderNames.LOCATION,
                Paths.USERS_FULL);

        return created();
    }

    /**
     * Validates that the dto has all necessary fields not empty.
     *
     * @param dto
     * @return true if valid
     */
    private static boolean validateUserDTO(NewUserWriteDTO dto) {
        return dto != null
                && dto.getUsername() != null && !dto.getUsername().trim().isEmpty()
                && dto.getUsername() != null && !dto.getUsername().trim().isEmpty();
    }

    @AuthRequired
    public static Result createTicket() {
        //Authenticated User can create ticket for websocket connection.

        HashMap<String, String> properties = new HashMap<>();
        String ticketNumber = ticketSystem.createTicket((User) ctx().args.get("currentuser"), null);

        response().setHeader(
                Http.HeaderNames.LOCATION,
                Paths.APPLICATION_SOCKET_TICKET + "/" + ticketNumber);

        return created();
    }

    public static WebSocket<String> connectToApplication(final String ticket) {
        //tickets.get(ticketNumber);
        Map<String, String> desiredProperties = new HashMap<>();
        if (! ticketSystem.validate(ticket, desiredProperties)){
            //TODO timestamp expiration would be necessary for real system.
            return WebSocket.reject(forbidden());
        }

        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
            @Override
            public Props apply(ActorRef outActor) throws Throwable {
                User userForValidTicket = ticketSystem.invalidate(ticket);
                return Props.create(ListSocketInActor.class, outActor, userForValidTicket);
            }
        });
    }

    @AuthRequired
    public static Result getOnlineList() {
        Set<User> users = ApplicationActor.getOnlineList();

        List<UserReadDTO> dtos = new ArrayList<>();
        for (User u : users) {
            dtos.add(UserMapper.mapToReadDTO(u));
        }

        return ok(Json.toJson(dtos));
    }
}
