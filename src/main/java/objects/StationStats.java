package objects;

public class StationStats {
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
        System.out.println("At this station, there were:");
        System.out.println(this.numFullFastCharges + " fast charges that received all desired energy");
        System.out.println(this.numFullSlowCharges + " slow charges that received all desired energy");
        System.out.println(this.numPartialFastCharges + " fast charges that received some desired energy");
        System.out.println(this.numPartialSlowCharges + " slow charges that received some desired energy");
        System.out.println(this.numNoFastCharges + " fast charges that received no energy");
        System.out.println(this.numNoSlowCharges + " slow charges that received no energy");
        System.out.println(this.numFaskBalks + " fast charges that got impatient");
        System.out.println(this.numSlowBalks + " slow charges that got impatient");
    }
}
