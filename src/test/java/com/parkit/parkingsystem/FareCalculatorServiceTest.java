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
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (90 * 60 * 1000) ); // Adding 30 extra minutes to exclude the 30 free minutes from the calculation
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(ticket.getPrice(), (60 * Fare.CAR_RATE_PER_MINUTE));
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (90 * 60 * 1000) ); // Adding 30 extra minutes to exclude the 30 free minutes from the calculation
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
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
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
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (45 * 60 * 1000) );//45 minutes parking time should give 1/4th parking fare because of the free 30 minutes
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals((15 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (45 * 60 * 1000) );//45 minutes parking time should give 1/4th parking fare because of the free 30 minutes
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals( (15 * Fare.CAR_RATE_PER_MINUTE) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  1470 * 60 * 1000) );//24 hours parking time should give 1440 minutes (+30 free minutes) * parking fare per hour
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
        // Arrange
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true); // Create a valid ParkingSpot
        ticket.setParkingSpot(parkingSpot); // Assign the ParkingSpot to the Ticket
        ticket.setInTime(new Date());
        ticket.setOutTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); // Adding 30 minutes

        // Act
        fareCalculatorService.calculateFare(ticket, false);

        // Assert
        assertEquals(0.0, ticket.getPrice()); // Expecting price to be 0.0 for the first 30 minutes
    }
    @Test
    public void calculateFare_ReturningCustomerDiscount() {
        // Arrange
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - 1470 * 60 * 1000)); //24 hours parking time should give 1440 minutes (+30 free minutes) * parking fare per hour
        ticket.setOutTime(new Date());
        boolean isReturningCustomer = true;

        ParkingSpot mockParkingSpot = mock(ParkingSpot.class);
        when(mockParkingSpot.getParkingType()).thenReturn(ParkingType.CAR); // Mocking the getParkingType() method

        ticket.setParkingSpot(mockParkingSpot);

        // Act
        fareCalculatorService.calculateFare(ticket, isReturningCustomer);

        // Assert
        double expectedPrice = ((ticket.getDuration() * Fare.CAR_RATE_PER_MINUTE) - 0.75) * (Fare.RETURNING_CUSTOMER_DISCOUNT);
        assertEquals(expectedPrice, ticket.getPrice());
    }
}
