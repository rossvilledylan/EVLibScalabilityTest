package objects;

import java.time.Instant;

public class GenEvent implements Event{
    private final Instant timestamp;
    private final double arrivalRate;
    public GenEvent(Instant stamp, double arrivalRate){
        this.timestamp = stamp;
        this.arrivalRate = arrivalRate;
    }
    public Instant getTimestamp(){
        return timestamp;
    }
    public double getArrivalRate(){ return arrivalRate; }
}
