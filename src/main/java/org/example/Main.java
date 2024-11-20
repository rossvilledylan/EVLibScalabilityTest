package org.example;

import resources.GlobalTime;
import evlib.sources.*;
import evlib.station.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        //Master config file that points to individual station configs that are passed to stations
        GlobalTime gT = new GlobalTime(8,0,00,22,0,00);
        int[] kinds = { 4,4 }; //The first should be the number of fast chargers, the second the number of slow chargers
        String[] sources = { "Geothermal", "Nonrenewable", "Wind", "Wave" };
        double[][] energyAm = new double[4][5];
        for (int i = 0; i<4; i++)
            for (int j = 0; j<5; j++)
                energyAm [i][j] = 1500;
        StationSimulator a = new StationSimulator("Miami",kinds,sources,energyAm, 13, gT); //Arrival Rate is cars per hour

    }
}