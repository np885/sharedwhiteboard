import actors.ApplicationActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import play.*;
import play.Application;
import play.libs.Akka;

/**
 * Created by niclas on 12.05.15.
 */
public class Global extends GlobalSettings{

    private ActorRef applicationActorRef;

    @Override
    public void onStart(Application application) {
        Logger.info("Application started :-) Global Object active!");
        applicationActorRef = Akka.system().actorOf(Props.create(ApplicationActor.class), ApplicationActor.NAME);
    }

    @Override
    public void onStop(Application application) {
        Logger.info("Application stops; Global Object poisining ApplicationActor!");
        applicationActorRef.tell(PoisonPill.getInstance(), applicationActorRef);

    }
}
