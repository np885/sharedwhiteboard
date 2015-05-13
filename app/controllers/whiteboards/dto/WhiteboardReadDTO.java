package controllers.whiteboards.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import controllers.common.Paths;
import controllers.common.dto.XHref;

import java.util.ArrayList;
import java.util.List;

/**
 */
@JsonPropertyOrder({"href"})
public class WhiteboardReadDTO {
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String href;

    private String name;
    private long id;

    private XHref owner;
    private List<XHref> collaborators = new ArrayList<>();
    private XHref socket;

    public XHref getSocket() {
        return socket;
    }

    public void setSocket(XHref socket) {
        this.socket = socket;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.href = Paths.forWhiteboardId(id);
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
