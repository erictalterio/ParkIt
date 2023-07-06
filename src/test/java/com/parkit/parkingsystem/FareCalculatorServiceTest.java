package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        // Test case for calculating fare for a car
        Date inTime = new Date(); // Creates a new Date object representing the current time and assigns it to the inTime variable.
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) ); // Sets the time of inTime to 1 hour (60 minutes) ago from the current time.
        Date outTime = new Date(); //  Creates a new Date object representing the current time and assigns it to the outTime variable.
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false); // Creates a new ParkingSpot object representing a car parking spot with ID n°1, ParkingType as CAR, and availability set to false to indicate a car is parked. It is assigned to the parkingSpot variable.

        ticket.setInTime(inTime); // Sets the inTime of the ticket object to the previously created inTime object.
        ticket.setOutTime(outTime); // Sets the outTime of the ticket object to the previously created outTime object.
        ticket.setParkingSpot(parkingSpot); // Sets the parkingSpot of the ticket object to the previously created parkingSpot object.
        fareCalculatorService.calculateFare(ticket, false); // Calls the calculateFare method of fareCalculatorService and calculates the fare for the given ticket and returning customer status (false in this case).
        assertEquals(ticket.getPrice(), (60 * Fare.CAR_RATE_PER_MINUTE)); // : Asserts that the calculated price of the ticket is equal to 60 minutes multiplied by the rate per minute for a car
    }

    @Test
    public void calculateFareBike(){
        // Test case for calculating fare for a bike
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(ticket.getPrice(), (60 * Fare.BIKE_RATE_PER_MINUTE));
    }

    @Test
    public void calculateFareUnkownType(){
        // Test case for calculating fare with an unknown vehicle type
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - ( 60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        // Test case for calculating fare with a future in-time (not allowed)
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        // Test case for calculating fare for a bike with less than one hour of parking time
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals((45 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        // Test case for calculating fare for a car with less than one hour of parking time
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals( (45 * Fare.CAR_RATE_PER_MINUTE) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        // Test case for calculating fare for a car with more than a day of parking time
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  1440 * 60 * 1000) );//24 hours parking time should give 1440 minutes * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals( (1440 * Fare.CAR_RATE_PER_MINUTE) , ticket.getPrice());
    }

    @Test
    public void calculateFare_First30MinutesFree() {
        // Test case for calculating fare with the first 30 minutes being free
        // Arrange
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot); 
        ticket.setInTime(new Date());
        ticket.setOutTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000));

        // Act
        fareCalculatorService.calculateFare(ticket, false);

        // Assert
        assertEquals(0.0, ticket.getPrice()); // Expecting price to be 0.0 for the first 30 minutes
    }

    @Test
    public void calculateFare_ReturningCustomerDiscount() {
        // Test case for calculating fare with a returning customer discount
        // Arrange
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - 1440 * 60 * 1000)); //24 hours parking time should give 1440 minutes (+30 free minutes) * parking fare per hour
        ticket.setOutTime(new Date());
        boolean isReturningCustomer = true;

        ParkingSpot mockParkingSpot = mock(ParkingSpot.class);
        when(mockParkingSpot.getParkingType()).thenReturn(ParkingType.CAR); // Mocking the getParkingType() method

        ticket.setParkingSpot(mockParkingSpot);

        // Act
        fareCalculatorService.calculateFare(ticket, isReturningCustomer);

        // Assert
        double expectedPrice = ((ticket.getDuration() * Fare.CAR_RATE_PER_MINUTE)) * (Fare.RETURNING_CUSTOMER_DISCOUNT);
        assertEquals(expectedPrice, ticket.getPrice());
    }
}
