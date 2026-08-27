package main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RentalSystemTest {

    private RentalSystem system;
    private final LocalDateTime start = LocalDateTime.of(2026, 6, 1, 9, 0);
    // start + 3 days
    private final LocalDateTime end = LocalDateTime.of(2026, 6, 4, 9, 0);

    @BeforeEach
    void setUp() {
        system = new RentalSystem();
        system.addCar(new Car("SEDAN-1", CarType.SEDAN));
        system.addCar(new Car("SEDAN-2", CarType.SEDAN));
        system.addCar(new Car("SUV-1", CarType.SUV));
        system.addCar(new Car("VAN-1", CarType.VAN));
    }

    @Test
    void reservingAvailableCarSucceeds() {
        Reservation r = system.reserveCar(CarType.SEDAN, start, end, "cust-1");
        assertNotNull(r);
        assertEquals(CarType.SEDAN, r.getCar().getType());
    }

    // Tests basic overlapping reservation on same car type
    @Test
    void secondNonOverlappingReservationOnSameCarTypeSucceeds() {
        LocalDateTime firstEnd = start.plusDays(2);
        Reservation first = system.reserveCar(CarType.SEDAN, start, firstEnd, "cust-1");

        Reservation second = system.reserveCar(
                CarType.SEDAN, firstEnd.plusDays(1), firstEnd.plusDays(3), "cust-2");

        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first.getId(), second.getId());
    }

    // Tests if it will get next available car if first car is taken via overlap
    @Test
    void overlappingReservationFallsBackToADifferentCarOfSameType() {
        Reservation first = system.reserveCar(CarType.SEDAN, start, end, "cust-1");
        Reservation second = system.reserveCar(
                CarType.SEDAN, start.plusDays(1), start.plusDays(2), "cust-2");

        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first.getCar().getId(), second.getCar().getId());
    }

    // Tests if returns null when no car available due to overlap
    @Test
    void exhaustingAllCarsOfATypeReturnsNullWhenOverlapping() {
        assertNotNull(system.reserveCar(CarType.SEDAN, start, end, "cust-1"));
        assertNotNull(system.reserveCar(CarType.SEDAN, start, end, "cust-2"));

        Reservation third = system.reserveCar(
                CarType.SEDAN, start.plusDays(1), start.plusDays(2), "cust-3");
        assertNull(third);
    }

    // Request inventory from empty system returns null
    @Test
    void requestingCarTypeWithNoInventoryReturnsNull() {
        RentalSystem emptySystem = new RentalSystem(); // no cars added at all
        Reservation r = emptySystem.reserveCar(CarType.VAN, start, start.plusDays(1), "cust-1");
        assertNull(r);
    }

    // Tests cancel reservation feature and if it allows for rebooking after cancelling
    @Test
    void cancellingAReservationFreesTheCarForRebooking() {
        Reservation r1 = system.reserveCar(CarType.SEDAN, start, end, "cust-1");
        system.reserveCar(CarType.SEDAN, start, end, "cust-2");
        assertNull(system.reserveCar(CarType.SEDAN, start, end, "cust-3"));

        system.cancelReservation(r1.getId());

        Reservation r3 = system.reserveCar(CarType.SEDAN, start, end, "cust-3");
        assertNotNull(r3);
        assertEquals(r1.getCar().getId(), r3.getCar().getId());
    }

    // Tests if program crashes when canceling reservation of unknown origin
    @Test
    void cancellingUnknownReservationDoesNotThrow() {
        assertDoesNotThrow(() -> system.cancelReservation("does-not-exist"));
    }

    // Tests if double cancelling a reservation crashes the program
    @Test
    void cancellingTheSameReservationTwiceDoesNotThrow() {
        Reservation r = system.reserveCar(CarType.SEDAN, start, end, "cust-1");
        system.cancelReservation(r.getId());
        assertDoesNotThrow(() -> system.cancelReservation(r.getId()));
    }

    // Tests if get available returns correct size for given type
    @Test
    void getAvailableReturnsOnlyFreeCarsOfRequestedType() {
        system.reserveCar(CarType.SEDAN, start, end, "cust-1");

        ArrayList<Car> availableSedans = system.getAvailable(start, end, CarType.SEDAN);
        assertNotNull(availableSedans);
        assertEquals(1, availableSedans.size());

        ArrayList<Car> availableSuvs = system.getAvailable(start, end, CarType.SUV);
        assertNotNull(availableSuvs);
        assertEquals(1, availableSuvs.size());
    }

    // Tests if get available returns null when none available
    @Test
    void getAvailableReturnsNullWhenNoneFree() {
        system.reserveCar(CarType.VAN, start, end, "cust-1"); // only 1 van in fleet
        ArrayList<Car> available = system.getAvailable(start, end, CarType.VAN);
        assertNull(available);
    }

    // Tests if get reservation by id returns a reservation
    @Test
    void getReservationReturnsBookedReservation() {
        Reservation r = system.reserveCar(CarType.SUV, start, end, "cust-1");
        Reservation lookedUp = system.getReservation(r.getId());
        assertEquals(r, lookedUp);
    }

    // Tests if get reservation by id returns null with invalid id
    @Test
    void getReservationReturnsNullForUnknownId() {
        assertNull(system.getReservation("does-not-exist"));
    }
}