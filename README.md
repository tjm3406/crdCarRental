# Car Rental System

A simulated car rental system built for a technical assessment. Supports
reserving a car of a given type at a desired date/time for a number of days,
with a limited number of cars per type.

## How to run

Built with plain Java (no Maven/Gradle) — JDK 17+, JUnit 5 for tests.

**Compile and run the app:**
```powershell
javac -d out src/main/*.java
java -cp out main.Main
```

**Run the tests:**
The Jar file is included in the github repository. The easiest way to run the tests is via
the IDE I used during this project (IntelliJ).

## Design
- CarType: enum that can easily support many types of cars. Right now supports SEDAN, SUV, VAN
- Car: An object class to represent a single vehicle. Has a field for a list of its reservations. Has isAvailable method to check for time range overalps
- Reservation: An object class that connects the car, start times, end times, and customer ID
- RentalSystem: The system manager. Adds cars, finds available cars, books/reserves cars, cancels reservations and looks up reservations by ID

## Key Design Decisions
- Availability is tracked per car object. This allows the ability to distinguish two non-overlapping bookings from two overlapping bookings.
- The overlap check used a half-open interval. This means that a reservation made starting at 1 pm and another reservation ending at 1 pm DO NOT conflict.
- Cancellation deletes the reservation from the system. This is for simplicity's sake but has trade-offs, discussed in limitations.

## Tests
Simple Junit tests were made to test the overlap algorithm and booking manager.

## Limitations
- Due to time constraints, some methods return null on failure. This is mentioned in the comments and there is a TODO to add exception handling. Exception handling would make scaling and future code easier to write because of constant need to null check.
- No thread safety, assumes single thread use. If concurrency is needed, then would need to lock around the booking sequence in reserveCar()

## Future Work
- Pricing and/or make/model of car. Would add to car object.
- Multiple locations, could add location field to car and fix manager code to add a location check
- Reserve any option for customers who have no preference. This would be an override method on reserve car
- Turnaround buffer could be added if there was a turnaround time needed between car rentals. Ie. Planned maintenance, cleaning/detailing, etc.
