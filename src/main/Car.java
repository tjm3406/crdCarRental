package main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Car {
    private final String id;
    private final CarType type;
    private ArrayList<Reservation> reservations;

    public Car(String id, CarType type) {
        this.type = type;
        this.id = id;
        reservations = new ArrayList<Reservation>();
    }

    public void addReservation(Reservation res) {
        this.reservations.add(res);
    }

    /**
     * This method checks the overlap of time ranges and if this car is available at given times
     * @param start1 start time for new reservation to check
     * @param end1 end time for new reservation to check
     * @return returns true if is available, false otherwise.
     * Another way to say this is returns FALSE if the new reservation has a start time before the end of ANY
     * current reservation AND the start time of ANY current reservation is before the new reservation end time
     */
    public boolean isAvailable(LocalDateTime start1, LocalDateTime end1) {
        for(Reservation res : this.reservations) {
            LocalDateTime start2 = res.getStart();
            LocalDateTime end2 = res.getEnd();
            if (start1.isBefore(end2) && start2.isBefore(end1)) {
                return false;
            }
        }
        return true;
    }

    public void cancelReservation(Reservation res) {
        this.reservations.remove(res);
    }

    public CarType getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

}
