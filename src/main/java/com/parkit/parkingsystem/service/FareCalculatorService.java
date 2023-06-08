package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date inTime = dateFormat.parse(dateFormat.format(ticket.getInTime()));
            Date outTime = dateFormat.parse(dateFormat.format(ticket.getOutTime()));

            long durationMillis = outTime.getTime() - inTime.getTime();
            int durationMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(durationMillis);

            if (durationMinutes <= 30) {
                ticket.setPrice(0); // Set fare to 0 if duration is less than or equal to 30 minutes
            } else {
                long durationHours = TimeUnit.MILLISECONDS.toHours(durationMillis);

            switch (ticket.getParkingSpot().getParkingType()){
                case CAR: {
                    ticket.setPrice(durationHours * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(durationHours * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default: throw new IllegalArgumentException("Unknown Parking Type");
                }
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing date: " + e.getMessage());
        }
    }
}
