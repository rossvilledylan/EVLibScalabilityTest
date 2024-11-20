package resources;

import java.time.Instant;

// For stats-taking purposes, the departure event needs to know not only when the car leaves the station and when it was
// Serviced, but also when it arrived, so these values can be compared

public class DepartureEvent implements Event {
    private final Instant timestamp; // Time the car leaves the charging station
    private final Instant arrivalTime;
    private final Instant serviceTime;
    private final String chargeType;
    public DepartureEvent(Instant stamp, Instant arrivalTime, Instant serviceTime, String chargeType){
        this.timestamp = stamp;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.chargeType = chargeType;
    }

    @Override
    public Instant getTimestamp(){
        return timestamp;
    }
    public Instant getArrivalTime() { return arrivalTime; }
    public Instant getServiceTime() { return serviceTime; }
    public String getChargeType() { return chargeType; }
}
