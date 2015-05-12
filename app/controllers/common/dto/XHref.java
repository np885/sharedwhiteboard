package controllers.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * eXtended Hyper Reference.<br/>
 * With only an href and a rel it's just a link. If more semantic is needed you can add other attributes. Nulled
 * Values won't be rendered.
 */
public class XHref {
    public static final String
            GET = "GET",
            PUT = "PUT",
            POST = "POST",
            DELETE = "DELETE";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String method;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String href;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object description;


    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getDescription() {
        return description;
    }

    /**
     * @param description <i>For PUT or POST requests: description will automatically be wrapped
     *                    into a {@link TemplateWrapperDTO} to serve the client as requestbody template</i>
     */
    public void setDescription(Object description) {
        if (method != null && description != null && (method.equals(POST) || method.equals(PUT))) {
            this.description = new TemplateWrapperDTO(description);
        } else {
            this.description = description;
        }
    }

    /**
     * @param rel   relation to the current resource
     * @param href  the url of the linked resource
     * @param method    HTTP-Method, if nulled the client will probably assume GET. Possible values: {@link XHref#GET},
     * {@link XHref#PUT}, {@link XHref#POST}, {@link XHref#DELETE}
     * @param description optionally used for telling the client the semantics of the linked resource. In the
     *                    easiest case a String with human readable description. In Collections it should be an object
     *                    with at least one key-value pair, so that the client can use it to find the searched item
     *                    in the collection. For PUT and POST, add a object as template for the PUT/POST-Requestbody
     *                    (<i>it will automatically be wrapped into a {@link TemplateWrapperDTO}</i>)
     */
    public XHref(String rel, String href, String method, Object description) {
        this.rel = rel;
        this.href = href;
        this.method = method;
        setDescription(description);
    }
}
