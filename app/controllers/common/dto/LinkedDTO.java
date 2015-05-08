package controllers.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * DTOs with Action-Lists.
 */
public interface LinkedDTO {

    List<XHref> getActions();
}
