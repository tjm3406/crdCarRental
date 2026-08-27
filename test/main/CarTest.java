package main;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    private final LocalDateTime start = LocalDateTime.of(2026, 6, 1, 9, 0);

    // Check if car with no reservations returns as available
    @Test
    void freshCarIsAvailableForAnyRange() {
        Car car = new Car("C1", CarType.SEDAN);
        assertTrue(car.isAvailable(start, start.plusDays(3)));
    }

    // Check if identical times return not available
    @Test
    void identicalRangeConflicts() {
        Car car = new Car("C1", CarType.SEDAN);
        LocalDateTime end = start.plusDays(3);
        car.addReservation(new Reservation(car, start, end, "cust-1"));

        assertFalse(car.isAvailable(start, end));
    }

    // Check if there is a partial overlap return not available
    @Test
    void partialOverlapConflicts() {
        Car car = new Car("C1", CarType.SEDAN);
        car.addReservation(new Reservation(car, start, start.plusDays(3), "cust-1"));

        assertFalse(car.isAvailable(start.plusDays(1), start.plusDays(5)));
    }

    // Check if there is overlap both before and after return not available (1st is larger)
    @Test
    void fullyContainedRangeConflicts() {
        Car car = new Car("C1", CarType.SEDAN);
        car.addReservation(new Reservation(car, start, start.plusDays(5), "cust-1"));

        assertFalse(car.isAvailable(start.plusDays(1), start.plusDays(2)));
    }

    // Check if there is overlap before and after return not available (2nd is larger)
    @Test
    void requestedRangeFullyContainingExistingReservationConflicts() {
        Car car = new Car("C1", CarType.SEDAN);
        car.addReservation(new Reservation(car, start.plusDays(1), start.plusDays(2), "cust-1"));

        // requested range fully swallows the existing (shorter) reservation
        assertFalse(car.isAvailable(start, start.plusDays(5)));
    }

    // Check if there is a request immediately after one ends then return available
    @Test
    void adjacentRangeEndingWhenOtherStartsDoesNotConflict() {
        Car car = new Car("C1", CarType.SEDAN);
        LocalDateTime firstEnd = start.plusDays(2);
        car.addReservation(new Reservation(car, start, firstEnd, "cust-1"));

        assertTrue(car.isAvailable(firstEnd, firstEnd.plusDays(2)));
    }

    // Check if there is a request immediately before another one starts then return available
    @Test
    void adjacentRangeStartingWhenOtherEndsDoesNotConflict() {
        Car car = new Car("C1", CarType.SEDAN);
        LocalDateTime secondStart = start.plusDays(2);
        car.addReservation(new Reservation(car, secondStart, secondStart.plusDays(2), "cust-1"));

        assertTrue(car.isAvailable(start, secondStart));
    }

    // Check if there is an overlap conflict of one minute, return not available
    @Test
    void oneMinuteOverlapConflicts() {
        Car car = new Car("C1", CarType.SEDAN);
        LocalDateTime firstEnd = start.plusDays(2);
        car.addReservation(new Reservation(car, start, firstEnd, "cust-1"));

        // requested range starts 1 minute before the existing one ends
        assertFalse(car.isAvailable(firstEnd.minusMinutes(1), firstEnd.plusDays(1)));
    }

    // Check completely different/disjointed ranges return available
    @Test
    void disjointRangeDoesNotConflict() {
        Car car = new Car("C1", CarType.SEDAN);
        car.addReservation(new Reservation(car, start, start.plusDays(2), "cust-1"));

        assertTrue(car.isAvailable(start.plusDays(5), start.plusDays(7)));
    }

    // Check cancellation method and if it frees up the car within that range
    @Test
    void cancellingReservationFreesTheCarForThatRange() {
        Car car = new Car("C1", CarType.SEDAN);
        LocalDateTime end = start.plusDays(3);
        Reservation res = new Reservation(car, start, end, "cust-1");
        car.addReservation(res);

        assertFalse(car.isAvailable(start, end));
        car.cancelReservation(res);
        assertTrue(car.isAvailable(start, end));
    }

    // Check multiple reservations and if various overlapping and non-overlapping time ranges will return correctly
    @Test
    void multipleReservationsAllBookCorrectly() {
        Car car = new Car("C1", CarType.SEDAN);
        car.addReservation(new Reservation(car, start, start.plusDays(2), "cust-1"));
        car.addReservation(new Reservation(car, start.plusDays(5), start.plusDays(7), "cust-2"));

        assertTrue(car.isAvailable(start.plusDays(2), start.plusDays(5)));  // gap between them
        assertFalse(car.isAvailable(start.plusDays(1), start.plusDays(3))); // hits first
        assertFalse(car.isAvailable(start.plusDays(6), start.plusDays(8))); // hits second
    }
}