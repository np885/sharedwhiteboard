package controllers.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flo on 06.05.2015.
 */
public interface LinkedDTO {

    List<XHref> getActions();
}
