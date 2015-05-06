package controllers.common.dto;

/**
 * Created by Flo on 06.05.2015.
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
