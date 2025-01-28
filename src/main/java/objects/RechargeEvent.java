package objects;

import java.time.Instant;

public class RechargeEvent implements Event{
    private final Instant timestamp;
    private final double[] charges;
    public RechargeEvent(Instant stamp, double[] charges){
        this.timestamp = stamp;
        this.charges = charges;
    }

    public Instant getTimestamp(){
        return timestamp;
    }
    public double[] getCharges(){
        return charges;
    }
}
