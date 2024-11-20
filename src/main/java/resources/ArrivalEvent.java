package resources;

import java.time.Instant;
import evlib.ev.*;

public class ArrivalEvent implements Event {
    private final Instant timestamp;
    private final String chargeType;
    private final ElectricVehicle veh;
    private final int chargeDesired;
    public ArrivalEvent(Instant stamp, String chargeType, int remainingAmount, int desireAmount, int capacityAmount){
        this.timestamp = stamp;
        this.chargeType = chargeType;
        Driver a = new Driver("a"); //Name is irrelevant, for our simulation
        this.veh = new ElectricVehicle("Honda");
        this.chargeDesired = desireAmount;
        Battery bat = new Battery(remainingAmount,capacityAmount); //This is the remaining amount and capacity of the battery, probably will be passed as an argument
        this.veh.setBattery(bat);
        this.veh.setDriver(a);
    }

    @Override
    public Instant getTimestamp(){
        return timestamp;
    }
    public String getChargeType() { return chargeType;}
    public ElectricVehicle getVeh() { return this.veh; }
    public int getChargeDesired() { return this.chargeDesired; }
}
