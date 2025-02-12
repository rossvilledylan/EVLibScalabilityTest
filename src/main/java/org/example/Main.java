package org.example;

import objects.GlobalTime;
import objects.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileWriter;

import java.util.concurrent.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        //Master config file that points to individual station configs that are passed to stations
        //
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("config/config.json");
            if(inputStream == null){
                throw new IOException("Config file not found in resources");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);
            String[] configFilesList = rootNode.get("configFiles").traverse(mapper).readValueAs(String[].class);
            GlobalTime gT = new GlobalTime(rootNode.get("startTimeHr").asInt(), rootNode.get("startTimeMin").asInt(), rootNode.get("startTimeSec").asInt(), rootNode.get("runtime").asInt());

            //Serial version
            long startTime = System.nanoTime();
            /*for (int i = 0; i<configFilesList.length; i++){
                StationSimulator b = new StationSimulator(configFilesList[i], gT);
            }*/
            long endTime = System.nanoTime();
            long serialDuration = endTime-startTime;

            //Parallel Version
            ConcurrentHashMap<String, BlockingQueue<Message>> monitorToStationQueues = new ConcurrentHashMap<>();
            BlockingQueue<Message> stationToMonitorQueue = new LinkedBlockingQueue<>();
            startTime = System.nanoTime();
            executor.submit(() -> {
                new Monitor(gT, stationToMonitorQueue, monitorToStationQueues, configFilesList.length);
            });
            for (String config : configFilesList){
                inputStream = Main.class.getClassLoader().getResourceAsStream("config/"+config);
                if(inputStream == null){
                    throw new IOException("Station config file not found in resources");
                }
                rootNode = mapper.readTree(inputStream);
                BlockingQueue<Message> monitorToStationQueue = new LinkedBlockingQueue<>();
                monitorToStationQueues.put(rootNode.get("name").asText(),monitorToStationQueue);
                executor.submit(() -> {
                    new StationSimulator(config, gT, stationToMonitorQueue, monitorToStationQueue);
                });
            }
            executor.shutdown(); // Stop accepting new tasks
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); // Wait for all tasks to complete
                endTime = System.nanoTime();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            long pDuration = endTime-startTime;
            FileWriter writer = new FileWriter("simulatorReport.txt");
            writer.write("The Serial Version took " + serialDuration + " nanoseconds\n");
            writer.write("The Parallel Version took " + pDuration + " nanoseconds");
            writer.close();
        }catch (IOException e){
            System.out.println("The config file cannot be found");
        }

    }
}