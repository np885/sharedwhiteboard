package model.whiteboards.repositories;

import model.AlreadyExistsException;
import model.whiteboards.entities.Whiteboard;
import org.hibernate.QueryException;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Flo on 06.05.2015.
 */
public class WhiteboardRepo {

    public static List<Whiteboard> findAll() {
        try {
            return JPA.withTransaction(new F.Function0<List<Whiteboard>>() {
                @Override
                public List<Whiteboard> apply() throws Throwable {
                    return JPA.em().createQuery("SELECT DISTINCT w FROM Whiteboard w JOIN FETCH w.collaborators").getResultList();
                }
            });
        } catch (Throwable throwable) {
            Logger.error("", throwable);
            return Arrays.asList();
        }
    }

    public static void createWhiteboard(final Whiteboard wb) throws AlreadyExistsException {
        if (getWhiteboardForName(wb.getName()) != null) {
            throw new AlreadyExistsException(String.format("Whiteboard with name '%s' already exists", wb.getName()));
        }
        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws Throwable {
                JPA.em().persist(wb);
            }
        });
    }

    /**
     * @param wbName
     * @return null, if no Whiteboard found for the given name.
     */
    public static Whiteboard getWhiteboardForName(final String wbName) {
        try {
            return JPA.withTransaction(new F.Function0<Whiteboard>() {
                @Override
                public Whiteboard apply() throws Throwable {
                    List<Whiteboard> wbList = JPA.em().createQuery("SELECT w FROM Whiteboard w WHERE w.name = :wbName")
                            .setParameter("wbName", wbName)
                            .getResultList();

                    return (wbList.size() == 0) ? null : wbList.get(0);
                }
            });
        } catch (Throwable throwable) {
            Logger.error("", throwable);
            return null;
        }
    }
}
