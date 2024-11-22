package objects;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDateTime;

public class GlobalTime {
    private final Instant startInstant;
    private final Instant endInstant;
    // Constructor to start at the exact time the program launches
    public GlobalTime() {
        this.startInstant = Instant.now();
        this.endInstant = Instant.now();
    }

    // Constructor to start at a specified time (ignoring date but keeping current date's day/month/year)
    public GlobalTime(int hour, int minute, int second, int endHour, int endMinute, int endSecond) {
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
        currentDate = LocalDateTime.now(); // get current date
        customTime = LocalDateTime.of(
                currentDate.getYear(),
                currentDate.getMonth(),
                currentDate.getDayOfMonth(),
                endHour,
                endMinute,
                endSecond
        );
        this.endInstant = customTime.toInstant(ZoneOffset.UTC);
    }

    // Method to get the start time as an Instant
    public Instant getStartInstant() {
        return startInstant;
    }

    // Method to get elapsed time since start in seconds
    public long getElapsedSeconds() {
        return Instant.now().getEpochSecond() - startInstant.getEpochSecond();
    }

    public Instant getEndInstant(){
        return endInstant;
    }
}
