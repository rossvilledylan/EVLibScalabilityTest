package objects;

import java.time.Instant;

public class EndMessage implements Message{
    private final Instant timestamp;
    private final String sender;

    public EndMessage(Instant i, String s){
        this.timestamp = i;
        this.sender = s;
    }
    public Instant getTimestamp() {
        return this.timestamp;
    }
    public String getSender() {
        return this.sender;
    }
}
