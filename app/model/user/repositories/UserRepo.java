package model.user.repositories;

import model.user.UserAlreadyExistsException;
import model.user.entities.User;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;

import java.util.Arrays;
import java.util.List;

/**
 */
public class UserRepo {

    /**
     * @return all users of the database; list may be empty.
     */
    public static List<User> findAll() {
        try {
            return JPA.withTransaction(new F.Function0<List<User>>() {
                @Override
                public List<User> apply() {
                    return JPA.em().createQuery("SELECT u FROM User u").getResultList();
                }
            });
        } catch (Throwable throwable) {
            Logger.error(throwable.toString());
            return Arrays.asList(new User[0]);
        }
    }

    public static void createNewUser(final User userToSave) throws UserAlreadyExistsException {
        if (getUserForUsername(userToSave.getUsername()) != null) {
            throw new UserAlreadyExistsException();
        }
        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws UserAlreadyExistsException {
                JPA.em().persist(userToSave);
            }
        });
    }

    /**
     * @param searchedUsername
     * @return null, if no user with that username was found
     */
    public static User getUserForUsername(final String searchedUsername) {
        try {
            return JPA.withTransaction(new F.Function0<User>() {
                @Override
                public User apply() {
                    List<User> users = JPA.em().createQuery("SELECT u FROM User u WHERE u.username=:username")
                            .setParameter("username", searchedUsername)
                            .getResultList();
                    if (users.size() > 1) {
                        Logger.warn("several Users found for username " + searchedUsername);
                    }

                    return (users.size() > 0) ? users.get(0) : null;
                }
            });
        } catch (Throwable t) {
            Logger.error("", t);
            return null;
        }
    }
}
