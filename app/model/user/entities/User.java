package model.user.entities;

import model.AbstractEntity;
import model.user.UserAlreadyExistsException;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;
//import play.db.jpa.JPA;
//import play.libs.F;

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


}
