package objects;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDateTime;

/**
 * A class that keeps track of the Global Simulated Time. This is necessary to inform the Stations and the Monitor of the
 * exact moment the Simulation is meant to start and to end, in terms of simulated time.
 */
public class GlobalTime {
    private final Instant startInstant;
    private final Instant endInstant;

    /**
     * Constructor to create a Global Time object. The simulation time will start at the exact moment of real time the
     * Simulation is started at, then run for "runtime" seconds of simulated time.
     * @param runtime the simulated time, in seconds, the simulator is run for.
     */
    public GlobalTime(int runtime){
        this.startInstant = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        this.endInstant = startInstant.plusSeconds(runtime);
    }

    /**
     * Constructor to create a Global Time object. Global Time will start a specified hour, minute and second of the day
     * of running's date, then continue for a specified runtime, given in seconds.
     * @param hour the hour the simulation starts at.
     * @param minute the minute the simulation starts at.
     * @param second the second the simulation starts at.
     * @param runtime the simulated time, in seconds, the simulator is to run for.
     */
    public GlobalTime(int hour, int minute, int second, int runtime) {
        LocalDateTime currentDate = LocalDateTime.now(); // get current date
        LocalDateTime customTime = LocalDateTime.of(
                currentDate.getYear(),
                currentDate.getMonth(),
                currentDate.getDayOfMonth(),
                hour,
                minute,
                second
        );
        this.startInstant = customTime.toInstant(ZoneOffset.UTC); // Convert to Instant
        this.endInstant = startInstant.plusSeconds(runtime);
    }

    /**
     * @return the instant the Simulation begins at.
     */
    public Instant getStartInstant() {
        return startInstant;
    }

    /**
     * @return the elapsed time, in seconds, the simulation has been running for.
     */
    public long getElapsedSeconds() {
        return Instant.now().getEpochSecond() - startInstant.getEpochSecond();
    }

    /**
     * @return the instant the Simulation will end at.
     */
    public Instant getEndInstant(){
        return endInstant;
    }
}
