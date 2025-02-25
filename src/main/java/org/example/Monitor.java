package org.example;

import objects.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.Collections;

public class Monitor {
    private final GlobalTime gT;
    private final BlockingQueue<Message> stationToMonitorQueue;
    private final ConcurrentHashMap<String, BlockingQueue<Message>> monitorToStationQueues;
    private final HashMap<String, Instant> stationTimesheet;
    private Instant minGlobalTime;
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public Monitor (GlobalTime gT, BlockingQueue<Message> s, ConcurrentHashMap<String, BlockingQueue<Message>> m, int n){
        this.gT = gT;
        this.stationToMonitorQueue = s;
        this.monitorToStationQueues = m;
        this.minGlobalTime = gT.getStartInstant();
        this.stationTimesheet = new HashMap<>();
        monitorLoop();
    }

    public void monitorLoop(){
        try {
            while(!checkTiming()){
                Message msg = stationToMonitorQueue.take();
                if(msg instanceof TimingMessage) {
                    stationTimesheet.put(msg.getSender(), msg.getTimestamp()); //This ensures that a station essentially adds itself, as the first action a station takes in its event loop is to send a message to the monitor
                    Instant prevMinGlobalTime = minGlobalTime;
                    minGlobalTime = Collections.min(stationTimesheet.values());
                    if (minGlobalTime != null && !minGlobalTime.equals(prevMinGlobalTime)) {
                        broadcastGlobalMin(minGlobalTime);
                    }else{
                        minGlobalTime = prevMinGlobalTime;
                    }
                }else if (msg instanceof BalkMessage){
                    ArrayList<String> keys = new ArrayList<>(monitorToStationQueues.keySet());
                    keys.remove(msg.getSender());
                    String nextStation = keys.get(random.nextInt(keys.size()));
                    monitorToStationQueues.get(nextStation).add(msg);
                    stationTimesheet.put(nextStation,((BalkMessage) msg).getBalkEvent().getTimestamp()); //Ensures the monitor knows the station is backtracking
                }
            }
            System.out.println("All stations have reached end of time");
            for(BlockingQueue<Message> q : monitorToStationQueues.values())
                q.add(new EndMessage(this.gT.getEndInstant(), "Monitor")); //The monitor will only send an End Message when it ends. It uses a special kind of Message so that the Simulators know to continue running even after they get a minTime that is at/after the global end time
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public boolean checkTiming(){
        if(stationTimesheet.isEmpty()) //Prevents Monitor from ending if Stations haven't been made in main yet
            return false;
        for (Instant time: stationTimesheet.values()) {
            if (time.isBefore(gT.getEndInstant()))
                return false;
        }
        return true;
    }

    private void broadcastGlobalMin(Instant min){
        for (BlockingQueue<Message> q : monitorToStationQueues.values())
                q.add(new TimingMessage(min,"Monitor"));
    }
}
