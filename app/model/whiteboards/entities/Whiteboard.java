package model.whiteboards.entities;

import model.AbstractEntity;
import model.user.entities.User;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Flo on 06.05.2015.
 */
@Entity
public class Whiteboard extends AbstractEntity {

    @Length(max = 40, min = 4)
    @Column(unique = true, nullable = false)
    private String name = "";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany(mappedBy = "whiteboards")
    private Set<User> collaborators = new HashSet<>();

    @OneToMany(mappedBy = "whiteboard")
    private List<AbstractDrawObject> drawObjects = new ArrayList<>();


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

    public void setCollaborators(Set<User> collaborators) {
        this.collaborators = collaborators;
    }

    public List<AbstractDrawObject> getDrawObjects() {
        return drawObjects;
    }

    public void setDrawObjects(List<AbstractDrawObject> drawObjects) {
        this.drawObjects = drawObjects;
    }
}
