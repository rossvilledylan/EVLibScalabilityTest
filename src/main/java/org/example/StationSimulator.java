package org.example;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.*;
import objects.*;

import java.time.Instant;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import evlib.station.*;

public class StationSimulator {
    Queue<Event> eventQueue = new PriorityQueue<>(
            (e1, e2) -> e1.getTimestamp().compareTo(e2.getTimestamp())
    ); //This is a priority queue for any kind of event
    ChargingStation station;
    int fastChargers;
    int fastInUse;
    int slowChargers;
    int slowInUse;
    Queue<ArrivalEvent> fastQueue = new PriorityQueue<>(
            (e1, e2) -> e1.getTimestamp().compareTo(e2.getTimestamp())
    );
    Queue<ArrivalEvent> slowQueue = new PriorityQueue<>(
            (e1, e2) -> e1.getTimestamp().compareTo(e2.getTimestamp())
    );

    GlobalTime gT;
    Instant stationTime;
    private static final Random random = new Random();

    //Temporary, will be implemented into stats attribute somehow
    StationStats sS = new StationStats();

    public StationSimulator(String configFile, GlobalTime gT){
        this.gT = gT;
        stationTime = gT.getStartInstant();
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("config/"+configFile);
            if(inputStream == null){
                throw new IOException("Station config file not found in resources");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);
            fastChargers = rootNode.get("fastChargers").asInt();
            slowChargers = rootNode.get("slowChargers").asInt();
            String[] kinds = new String[fastChargers + slowChargers];
            for (int i = 0; i < fastChargers; i++)
                kinds[i] = "fast";
            for (int i = fastChargers; i < kinds.length; i++)
                kinds[i] = "slow";
            String[] sources = rootNode.get("energySources").traverse(mapper).readValueAs(String[].class);
            double[] enAm = rootNode.get("energyAmounts").traverse(mapper).readValueAs(double[].class);
            double[][] energyAmount = new double[sources.length][5];
            for (int i = 0; i < sources.length; i++)
                for (int j = 0; j < 5; j++)
                    energyAmount[i][j] = enAm[i];
            station = new ChargingStation(rootNode.get("name").asText(), kinds, sources, energyAmount);
            for (int i = 0; i < sources.length; i++)
                station.setSpecificAmount(sources[i],enAm[i]);
            station.setChargingRateFast(rootNode.get("fastChargingRate").asInt());
            station.setChargingRateSlow(rootNode.get("slowChargingRate").asInt());
            stationSetup();

            GenEvent c = new GenEvent(gT.getStartInstant(), rootNode.get("arrivalRate").asInt());
            eventQueue.add(c);
            eventLoop();

        } catch(IOException e){
            System.out.println("The requested station file does not exist");
        }
    }


    //StationSimulator constructor for if there is a specific set of values you want passed
    public StationSimulator(String name, int[] types, String[] sources, double[][] energyAmount, double arrivalRate, GlobalTime gT){ //Can pass arguments now, I guess
        this.gT = gT;
        stationTime = gT.getStartInstant(); //Set the station's current time

        fastChargers = types[0];
        slowChargers = types[1];
        String[] kinds = new String[types[0]+types[1]];
        for (int i = 0; i < fastChargers; i++)
            kinds[i] = "fast";
        for (int i = fastChargers; i < kinds.length; i++)
            kinds[i] = "slow";
        station = new ChargingStation(name, kinds, sources, energyAmount);
        station.setSpecificAmount("wind",100000);//Naieve setup
        stationSetup();

        GenEvent c = new GenEvent(gT.getStartInstant(), arrivalRate); //Create our first eventGenerator event
        eventQueue.add(c);
        eventLoop(); //Begin the event loop
    }


    public void eventLoop(){
        while(!eventQueue.isEmpty()) {
            Event e = eventQueue.remove();
            if (e instanceof GenEvent & this.stationTime.isBefore(this.gT.getEndInstant())) { //"isBefore" can be used to check if time is semantically before
                System.out.println("Gen Event Detected");
                this.stationTime = e.getTimestamp();
                System.out.println("The current time is: " + this.stationTime);
                genEvents(((GenEvent) e).getArrivalRate());
            } else if (e instanceof ArrivalEvent) {
                handleArrivalEvent((ArrivalEvent) e);
            } else if (e instanceof DepartureEvent){
                handleDepartureEvent((DepartureEvent) e);
                switch(((DepartureEvent) e).getStatus()){
                    case "Uncharged":
                        if(((DepartureEvent) e).getChargeType().equals("fast"))
                            sS.setNumNoFastCharges(sS.getNumNoFastCharges()+1);
                        else if(((DepartureEvent) e).getChargeType().equals("slow"))
                            sS.setNumNoSlowCharges(sS.getNumNoSlowCharges()+1);
                        break;
                    case "Partially Charged":
                        if(((DepartureEvent) e).getChargeType().equals("fast"))
                            sS.setNumPartialFastCharges(sS.getNumPartialFastCharges()+1);
                        else if(((DepartureEvent) e).getChargeType().equals("slow"))
                            sS.setNumPartialSlowCharges(sS.getNumPartialSlowCharges()+1);
                        break;
                    case "Fully Charged":
                        if(((DepartureEvent) e).getChargeType().equals("fast"))
                            sS.setNumFullFastCharges(sS.getNumFullFastCharges()+1);
                        else if(((DepartureEvent) e).getChargeType().equals("slow"))
                            sS.setNumFullSlowCharges(sS.getNumFullSlowCharges()+1);
                        break;
                }
                System.out.println("A Car requesting " + ((DepartureEvent) e).getChargeType() + " arrived at " + ((DepartureEvent) e).getArrivalTime() + ", was Serviced at " + ((DepartureEvent) e).getServiceTime() + " and left at " + e.getTimestamp() + ". The station time is " + this.stationTime + " and the global end time is " + gT.getEndInstant());
            }
            System.out.println("There are " + fastQueue.size() + " cars in the fast queue and " + fastInUse + " cars being fast charged");
            System.out.println("There are " + slowQueue.size() + " cars in the slow queue and " + slowInUse + " cars being slow charged");
            System.out.println(eventQueue);
            System.out.println();
        }
        sS.printStats();
        station.genReport("C:/School/Master's Project/OurProject/EVLibScalabilityTest/report.txt");
    }


    //This function handles the creation of events. Can be modified if we pass both arrival rate and avg. time between arrivals
    public void genEvents(double arrivalRate){
        double hourInSeconds = 3600;
        double avgInterArrivalTime = hourInSeconds/arrivalRate;
        Instant currentTime = this.stationTime;
        // **Determine how often a slow/fast charger is picked**
        while (true) {
            // Generate the next inter-arrival time in seconds
            double interArrivalTime = -Math.log(1 - random.nextDouble()) * avgInterArrivalTime;
            // Calculate the next event timestamp
            currentTime = currentTime.plusSeconds((long) interArrivalTime);
            // Stop if we exceed one hour
            if (currentTime.isAfter(this.stationTime.plusSeconds((long) hourInSeconds))) {
                break;
            }
            // Create a new ArrivalEvent and add it to the list
            // As of 11/14, arrival events are more likely to be fast than slow
            //
            // It appears that the numbers are measured in *watts*. 40000 watts is, according to Google, the average battery size
            int remaining = random.nextInt(40001);
            eventQueue.add(new ArrivalEvent(currentTime, random.nextDouble() < 0.67 ? "fast" : "slow",remaining,40000-remaining,40000));
        }
        GenEvent e = new GenEvent(this.stationTime.plusSeconds((long)hourInSeconds), arrivalRate);
        eventQueue.add(e);
    }

    public void handleArrivalEvent(ArrivalEvent a){
        this.stationTime = a.getTimestamp();
        if(a.getChargeType().equals("fast")){
            if(fastInUse >= fastChargers) {
                fastQueue.add(a);
            }
            else {
                fastInUse++;
                startCharge(a);
            }
        }
        else if(a.getChargeType().equals("slow")){
            if(slowInUse >= slowChargers) {
                slowQueue.add(a);
            }
            else {
                slowInUse++;
                startCharge(a);
            }
        }
    }

    public void handleDepartureEvent(DepartureEvent d){
        this.stationTime = d.getTimestamp();
        if(d.getChargeType().equals("fast")){
            // Take an event off the fast queue and put it on the charger
            if(!fastQueue.isEmpty()) {
                //This if statement is an "impatience" function that balks at 10 minutes
                ArrivalEvent a = fastQueue.peek();
                while(!fastQueue.isEmpty() && !a.getTimestamp().plusSeconds(600).isAfter(this.stationTime)) {
                    fastQueue.remove();
                    sS.setNumFaskBalks(sS.getNumFaskBalks() + 1);
                    if(!fastQueue.isEmpty()){
                        a = fastQueue.peek();
                    }
                }
                if(!fastQueue.isEmpty()){
                    a = fastQueue.remove();
                    startCharge(a);
                }
                else {
                    fastInUse--;
                }
            }
            else {
                fastInUse--;
            }
        }
        if(d.getChargeType().equals("slow")){
            if (!slowQueue.isEmpty()){
                // Take an event off the slow queue and put it on the charger
                ArrivalEvent a = slowQueue.peek(); // Peek to check the head without removing it
                while (!slowQueue.isEmpty() && !a.getTimestamp().plusSeconds(1800).isAfter(this.stationTime)) {
                    slowQueue.remove(); // Remove only if the condition is not satisfied
                    sS.setNumSlowBalks(sS.getNumSlowBalks()+1);
                    if (!slowQueue.isEmpty()) {
                        a = slowQueue.peek(); // Update 'a' with the next event
                    }
                }
                if (!slowQueue.isEmpty()) { // Check again if the queue has a valid event
                    a = slowQueue.remove(); // Remove the valid event
                    startCharge(a);         // Start charging
                }
                else {
                    slowInUse--;
                }
            }
            else {
                slowInUse--;
            }
        }
    }

    public void startCharge(ArrivalEvent a){
        ChargingEvent ev = new ChargingEvent(station, a.getVeh(),a.getChargeDesired(),a.getChargeType());

        //This is a very simplified version of ChargingEvent.java's energy checking formula. Cars never ask for more
        //energy than they can hold, so it is unnecessary to check.
        if (a.getChargeDesired() < station.getTotalEnergy()) {
            ev.setEnergyToBeReceived(a.getChargeDesired());
        } else {
            ev.setEnergyToBeReceived(station.getTotalEnergy());
        }
        if(ev.getEnergyToBeReceived() == 0) {
            DepartureEvent b = new DepartureEvent(a.getTimestamp(), a.getTimestamp(), this.stationTime, a.getChargeType(), "Uncharged");
            eventQueue.add(b);
            return;
        }

        ev.setEnergyToBeReceived(a.getChargeDesired());
        ev.setChargingTime(((long) (ev.getEnergyToBeReceived() * 3600000 / station.getChargingRateFast())));
        ev.setCost(station.calculatePrice(ev));
        ev.setCondition("ready");

        //station.setSpecificAmount("Wind",station.getMap().get("Wind")-ev.getEnergyToBeReceived());
        double sdf;
        sdf = a.getChargeDesired();
        //Determine which source of energy the car receives
        for (String s : station.getSources()) {
            if (sdf < station.getMap().get(s)) {
                double ert = station.getMap().get(s) - sdf;
                station.setSpecificAmount(s, ert);
                break;
            } else {
                sdf -= station.getMap().get(s);
                station.setSpecificAmount(s, 0);
            }
        }


        DepartureEvent b = new DepartureEvent(calcChargingTime(a.getChargeDesired(),ev.getKindOfCharging()), a.getTimestamp(), this.stationTime, a.getChargeType(), ev.getEnergyToBeReceived() < a.getChargeDesired() ? "Partially Charged" : "Fully Charged");
        eventQueue.add(b);
        station.assignCharger(ev);
        ev.execution();
    }

    public void stationSetup(){
        //Taken from github
        DisCharger dsc = new DisCharger(station);
        ExchangeHandler handler = new ExchangeHandler(station);
        ParkingSlot slot = new ParkingSlot(station);

        station.addExchangeHandler(handler);
        station.addDisCharger(dsc);
        station.addParkingSlot(slot);
        station.setAutomaticUpdateMode(false);
        station.updateStorage();
        station.setTimeofExchange(5000);
        station.setDisChargingRate(0.1);
        station.setInductiveChargingRate(0.001);

        station.setUnitPrice(5);
        station.setDisUnitPrice(5);
        station.setInductivePrice(3);
        station.setExchangePrice(20);

        return;
    }

    public Instant calcChargingTime(int charge, String type){
        return this.stationTime.plusSeconds(((long) (charge * 3600 / (type.equals("fast") ? station.getChargingRateFast() : station.getChargingRateSlow()))));
    }
}
