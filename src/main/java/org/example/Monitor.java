package org.example;

import objects.*;
import objects.Event.ArrivalEvent;
import objects.Message.BalkMessage;
import objects.Message.EndMessage;
import objects.Message.Message;
import objects.Message.TimingMessage;

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
    private final HashMap<ArrivalEvent, String> eventMapping; //This hashmap tracks where *arrival events* specifically are sent when a balk message is received.
    private Instant minGlobalTime;
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public Monitor (GlobalTime gT, BlockingQueue<Message> s, ConcurrentHashMap<String, BlockingQueue<Message>> m, int n){
        this.gT = gT;
        this.stationToMonitorQueue = s;
        this.monitorToStationQueues = m;
        this.minGlobalTime = gT.getStartInstant();
        this.stationTimesheet = new HashMap<>();
        this.eventMapping = new HashMap<>();
        monitorLoop();
    }

    public void monitorLoop(){
        try {
            while(minGlobalTime.isBefore(gT.getEndInstant()) || checkMessages()){
                Message msg = stationToMonitorQueue.take();
                if(msg instanceof TimingMessage) {
                    stationTimesheet.put(msg.getSender(), msg.getTimestamp()); //This ensures that a station essentially adds itself, as the first action a station takes in its event loop is to send a message to the monitor
                    Instant prevMinGlobalTime = minGlobalTime;
                    minGlobalTime = Collections.min(stationTimesheet.values());
                    if (minGlobalTime != null && !minGlobalTime.equals(prevMinGlobalTime)) {
                        broadcastGlobalMin(minGlobalTime);
                        //System.out.println("From the Monitor: global timesheet is as follows:\n" + stationTimesheet.keySet() + "\n" + stationTimesheet.values() + " " + minGlobalTime);
                    }else{
                        minGlobalTime = prevMinGlobalTime;
                    }
                }else if (msg instanceof BalkMessage){
                    if(((BalkMessage) msg).getRetread()) { //This handles messages needing to be re-done if a station backtracks
                        String stationToBacktrack = eventMapping.get(((BalkMessage) msg).getEventToLeave());
                        monitorToStationQueues.get(stationToBacktrack).add(msg);
                    }else {
                        ArrayList<String> keys = new ArrayList<>(monitorToStationQueues.keySet());
                        keys.remove(msg.getSender());
                        if(!keys.isEmpty()) {
                            String nextStation = keys.get(random.nextInt(keys.size()));
                            monitorToStationQueues.get(nextStation).add(msg);
                            eventMapping.put(((BalkMessage) msg).getEventToLeave(), nextStation);
                        }
                        //If there is only one station, the car simply leaves and does not get charged.
                    }
                }
            }
            //System.out.println("All stations have reached end of time\n" + gT.getEndInstant() + "\n" + stationTimesheet.values() + "\n" + monitorToStationQueues.keySet());
            for(BlockingQueue<Message> q : monitorToStationQueues.values())
                q.add(new EndMessage(this.gT.getEndInstant(), "Monitor")); //The monitor will only send an End Message when it ends. It uses a special kind of Message so that the Simulators know to continue running even after they get a minTime that is at/after the global end time
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }catch (Exception e){
            System.out.println("Monitor has exception " + e);
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
    public boolean checkMessages(){//ensures that there are no outstanding messages in each station's queue
        if(monitorToStationQueues.isEmpty())
            return false;
        for (BlockingQueue<Message> q : monitorToStationQueues.values())
            if(q.isEmpty())
                return false;
        return true;
    }

    private void broadcastGlobalMin(Instant min){
        for (BlockingQueue<Message> q : monitorToStationQueues.values())
                q.add(new TimingMessage(min,"Monitor"));
    }
}
