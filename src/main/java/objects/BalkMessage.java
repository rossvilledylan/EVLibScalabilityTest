package objects;

import java.time.Instant;

public class BalkMessage implements Message{
    private final Instant timestamp;
    private final String sender;
    private final ArrivalEvent balkEvent;

    public BalkMessage(Instant i, String s, ArrivalEvent a){
        this.timestamp = i;
        this.sender = s;
        this.balkEvent = a;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
    public String getSender(){
        return this.sender;
    }
    public ArrivalEvent getBalkEvent(){
        return this.balkEvent;
    }
}
