package controllers.whiteboards.dto;

import controllers.common.dto.XHref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flo on 06.05.2015.
 */
public class WhiteboardReadDTO {
    private String name;
    private long id;


    private XHref owner;
    private List<XHref> collaborators = new ArrayList<>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public XHref getOwner() {
        return owner;
    }

    public void setOwner(XHref owner) {
        this.owner = owner;
    }

    public List<XHref> getCollaborators() {
        return collaborators;
    }

}
