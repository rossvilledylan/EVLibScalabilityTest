package org.example;

import objects.GlobalTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        //Master config file that points to individual station configs that are passed to stations
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("config/config.json");
            if(inputStream == null){
                throw new IOException("Config file not found in resources");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);
            String[] configFilesList = rootNode.get("configFiles").traverse(mapper).readValueAs(String[].class);
            GlobalTime gT = new GlobalTime(rootNode.get("startTimeHr").asInt(), rootNode.get("startTimeMin").asInt(), rootNode.get("startTimeSec").asInt(), rootNode.get("endTimeHr").asInt(), rootNode.get("endTimeMin").asInt(), rootNode.get("endTimeSec").asInt());
            for (int i = 0; i<configFilesList.length; i++){
                StationSimulator b = new StationSimulator(configFilesList[i], gT);
            }

            //int[] kinds = {4, 4}; //The first should be the number of fast chargers, the second the number of slow chargers
            //String[] sources = {"Geothermal", "Nonrenewable", "Wind", "Wave"};
            //double[][] energyAm = new double[4][5];
            //for (int i = 0; i < 4; i++)
                //for (int j = 0; j < 5; j++)
                    //energyAm[i][j] = 1500;
            //StationSimulator a = new StationSimulator("Miami", kinds, sources, energyAm, 13, gT); //Arrival Rate is cars per hour
        }catch (IOException e){
            System.out.println("The config file cannot be found");
        }

    }
}