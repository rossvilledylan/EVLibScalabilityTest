package objects;

import java.time.Instant;

public class TimingMessage implements Message{
    private final Instant timestamp;
    private final String sender;

    public TimingMessage(Instant t, String s){
        this.timestamp = t;
        this.sender = s;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
    public String getSender(){
        return this.sender;
    }
}
