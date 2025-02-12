package objects;

import java.time.Instant;

public interface Message {
    Instant getTimestamp();
    String getSender();
}
