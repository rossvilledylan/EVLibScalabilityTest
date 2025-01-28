package objects;
import java.io.FileWriter;
import java.io.IOException;

public class StationStats {
    private String stationName;
    private int numFullFastCharges;
    private int numFullSlowCharges;
    private int numPartialFastCharges;
    private int numPartialSlowCharges;
    private int numNoFastCharges;
    private int numNoSlowCharges;
    private int numFaskBalks;
    private int numSlowBalks;

    public StationStats(){
        this.numFullFastCharges = 0;
        this.numFullSlowCharges = 0;
        this.numPartialFastCharges = 0;
        this.numPartialSlowCharges = 0;
        this.numNoFastCharges = 0;
        this.numNoSlowCharges = 0;
        this.numFaskBalks = 0;
        this.numSlowBalks = 0;
    }

    public int getNumFullFastCharges() {
        return numFullFastCharges;
    }
    public int getNumFullSlowCharges(){
        return numFullSlowCharges;
    }
    public int getNumPartialFastCharges() {
        return numPartialFastCharges;
    }
    public int getNumPartialSlowCharges() {
        return numPartialSlowCharges;
    }
    public int getNumNoFastCharges() {
        return numNoFastCharges;
    }
    public int getNumNoSlowCharges(){
        return numNoSlowCharges;
    }
    public int getNumFaskBalks() {
        return numFaskBalks;
    }
    public int getNumSlowBalks() {
        return numSlowBalks;
    }

    public void setStationName(String stationName){
        this.stationName = stationName;
    }
    public void setNumFullFastCharges(int numFullFastCharges){
        this.numFullFastCharges = numFullFastCharges;
    }
    public void setNumFullSlowCharges(int numFullSlowCharges) {
        this.numFullSlowCharges = numFullSlowCharges;
    }
    public void setNumPartialFastCharges(int numPartialFastCharges) {
        this.numPartialFastCharges = numPartialFastCharges;
    }
    public void setNumPartialSlowCharges(int numPartialSlowCharges) {
        this.numPartialSlowCharges = numPartialSlowCharges;
    }
    public void setNumNoFastCharges(int numNoFastCharges) {
        this.numNoFastCharges = numNoFastCharges;
    }
    public void setNumNoSlowCharges(int numNoSlowCharges) {
        this.numNoSlowCharges = numNoSlowCharges;
    }
    public void setNumFaskBalks(int numFaskBalks) {
        this.numFaskBalks = numFaskBalks;
    }
    public void setNumSlowBalks(int numSlowBalks) {
        this.numSlowBalks = numSlowBalks;
    }

    public void printStats(){
        try {
            FileWriter writer = new FileWriter(stationName + ".txt");
            writer.write("At this station, there were:\n");
            writer.write(this.numFullFastCharges + " fast charges that received all desired energy\n");
            writer.write(this.numFullSlowCharges + " slow charges that received all desired energy\n");
            writer.write(this.numPartialFastCharges + " fast charges that received some desired energy\n");
            writer.write(this.numPartialSlowCharges + " slow charges that received some desired energy\n");
            writer.write(this.numNoFastCharges + " fast charges that received no energy\n");
            writer.write(this.numNoSlowCharges + " slow charges that received no energy\n");
            writer.write(this.numFaskBalks + " fast charges that got impatient\n");
            writer.write(this.numSlowBalks + " slow charges that got impatient\n");
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
