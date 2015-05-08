package controllers.common.dto;

/**
 * Used for JSON serialization: Wrappes an object into a "template" key, so that the client can recognize it as a
 * POST or PUT template for sending data.
 */
public class TemplateWrapperDTO {
    private Object template;

    public TemplateWrapperDTO(Object template) {
        this.template = template;
    }

    public Object getTemplate() {
        return template;
    }

    public void setTemplate(Object template) {
        this.template = template;
    }
}
