package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private final String id;
    private Car car;
    private LocalDateTime start;
    private LocalDateTime end;
    private String customer_id;

    public Reservation (Car car, LocalDateTime start, LocalDateTime end, String customer_id) {
        this.id = UUID.randomUUID().toString();;
        this.car = car;
        this.start = start;
        this.end = end;
        this.customer_id = customer_id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getId() {
        return id;
    }

    public Car getCar() {
        return car;
    }
}
