package actors.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Abstract Super Class for everything that is serialized and sent via the WebSockets. The Client will
 * trust on the existence of properties of this class.
 */
@JsonPropertyOrder({"eventType"})
public abstract class AbstractSocketOutDTO {

    @JsonProperty
    public abstract String getEventType();
}
