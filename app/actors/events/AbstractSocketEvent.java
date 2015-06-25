package actors.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Abstract Super Class for everything that is deserialized/serialized and sent/received via the WebSockets. The Client will
 * trust on the existence of properties of this class and we trust on the client setting the proper type.
 */
@JsonPropertyOrder({"eventType"})
public abstract class AbstractSocketEvent implements SWBEvent {
    @JsonProperty
    public abstract String getEventType();

    @JsonIgnore
    //only for jaxon deserialization:
    private void setEventType(String eventType) {}
}
