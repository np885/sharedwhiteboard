package model.whiteboards.entities;

import model.AbstractEntity;
import model.user.entities.User;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Flo on 06.05.2015.
 */
@Entity
public class Whiteboard extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String name = "";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "whiteboard_collaborators")
    private Set<User> collaborators = new HashSet<>();

    @OneToMany(mappedBy = "whiteboard", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapKey(name = "boardElementId")
    private Map<Integer, AbstractDrawObject> drawObjects = new HashMap<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getCollaborators() {
        return collaborators;
    }

    public Map<Integer, AbstractDrawObject> getDrawObjects() {
        return drawObjects;
    }

    public void setCollaborators(Set<User> collaborators) {
        this.collaborators = collaborators;
    }
}
