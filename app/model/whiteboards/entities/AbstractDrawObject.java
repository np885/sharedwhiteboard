package model.whiteboards.entities;

import model.AbstractEntity;

import javax.persistence.*;

/**
 * Created by Flo on 06.05.2015.
 */
@Entity
@Table(name = "drawobjects")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class AbstractDrawObject extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "whiteboard_id")
    private Whiteboard whiteboard;

}
