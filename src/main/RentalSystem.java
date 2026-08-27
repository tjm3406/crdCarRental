package main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class RentalSystem {

    private HashMap<CarType, ArrayList<Car>> inventory = new HashMap<>();
    private HashMap<String, Reservation> reservationsById = new HashMap<>();

    public RentalSystem() {
        for (CarType type : CarType.values()) {
            inventory.put(type, new ArrayList<Car>());
        }
    }

    /** Adds a car to the fleet. Typically called during system setup. */
    public void addCar(Car car) {
        inventory.get(car.getType()).add(car);
    }

    /**
    * This method takes a start time, end time and car type for a reservation and returns a list of all available cars
    with those constraints.
     * Returns null and prints to console if no car is available
     TODO: Add exceptions instead of returning null
     */
    public ArrayList<Car> getAvailable(LocalDateTime start, LocalDateTime end, CarType type) {
        ArrayList<Car> available = new ArrayList<>();
        for(Car c : this.inventory.get(type)) {
            if(c.isAvailable(start, end)) {
                available.add(c);
            }
        }
        if(available.isEmpty()) {
            System.out.println("No cars of type " + type + " available");
            return null;
        }
        return available;
    }

    public void cancelReservation(String reservationId){
        Reservation reservation = reservationsById.remove(reservationId);
        if (reservation == null) {
            System.out.println("Attempted to cancel reservation of ID " + reservationId + " but reservation does not exist");
            return;
        }
        reservation.getCar().cancelReservation(reservation);
    }

    /**
     * This method takes a car type, start time, end time and customer ID. It makes a reservation if available.
     * Uses getAvailable method
     * Returns null if not available, returns Reservation object if available
     * TODO: Add exceptions instead of returning null
     */
    public Reservation reserveCar(CarType type, LocalDateTime start, LocalDateTime end, String customerID) {
        ArrayList<Car> available = getAvailable(start, end, type);

        if (available == null) {
            System.out.println("No cars of type " + type + " available. Request made from customer ID " + customerID);
            return null;
        }

        Car c = available.get(0);
        Reservation res = new Reservation(c, start, end, customerID);
        c.addReservation(res);
        reservationsById.put(res.getId(), res);
        return res;
    }

    public Reservation getReservation(String resID) {
        return reservationsById.get(resID);
    }
}
