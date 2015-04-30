package model.user.entities;

import model.AbstractEntity;
import model.user.UserAlreadyExistsException;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

/**
 * Created by Flo on 27.04.2015.
 */
@Entity
public class User extends AbstractEntity {

    @Column(unique = true)
    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    ///////////////////////
    // funktioniert noch nicht: beim persist fliegt "unknwon entity" .__.
    public void save() throws UserAlreadyExistsException {
        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws UserAlreadyExistsException {
//                if (findByUserName(username) != null) {
//                    throw new UserAlreadyExistsException();
//                }
                JPA.em().persist(this);
            }
        });
    }

    public static User findByUserName(String username) {
        List<User> users = JPA.em().createQuery("SELECT u FROM User u WHERE u.username=:username")
                .setParameter("username", username)
                .getResultList();
        if (users.size() > 0) {
            Logger.warn("several Users found for username " + username);
        }

        return (users.size() > 0) ? users.get(0) : null;
    }
}
