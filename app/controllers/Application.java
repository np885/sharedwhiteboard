package controllers;

import actors.ApplicationActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.libs.Akka;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

}
