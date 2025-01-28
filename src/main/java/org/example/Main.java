package org.example;

import objects.GlobalTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.io.IOException;

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
            for (int i = 0; i<configFilesList.length; i++){ //Parallelize this
                StationSimulator b = new StationSimulator(configFilesList[i], gT);
            }
            long endTime = System.nanoTime();
            long duration = endTime-startTime;
            System.out.println("The Serial Version took " + duration + " nanoseconds");

            //Parallel Version
            startTime = System.nanoTime();
            for (String config : configFilesList){ //Parallelize this
                executor.submit(() -> {
                    StationSimulator b = new StationSimulator(config, gT);
                });
            }
            executor.shutdown(); // Stop accepting new tasks
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); // Wait for all tasks to complete
                endTime = System.nanoTime();
                duration = endTime-startTime;
                System.out.println("The Parallel Version took " + duration + " nanoseconds");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }catch (IOException e){
            System.out.println("The config file cannot be found");
        }

    }
}