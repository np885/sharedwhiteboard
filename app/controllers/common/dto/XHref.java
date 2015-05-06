package controllers.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * eXtended Hyper Reference. With only an href and a rel it's just a link. If more semantic is needed you can add other attributes.
 * Created by Flo on 06.05.2015.
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

    public void setDescription(Object description) {
        if (method != null && method.equals(POST)) {
            this.description = new TemplateWrapperDTO(description);
        } else {
            this.description = description;
        }
    }

    public XHref(String rel, String href, String method, Object description) {
        this.rel = rel;
        this.href = href;
        this.method = method;
        setDescription(description);
    }
}
