package actors.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true) //tolerant read.
public interface SWBEvent {
}
