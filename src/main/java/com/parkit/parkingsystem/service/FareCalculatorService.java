package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.concurrent.TimeUnit;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean isReturningCustomer) {
        if (ticket.getInTime() == null || ticket.getOutTime() == null) {
            throw new IllegalArgumentException("Invalid Ticket: InTime or OutTime is null");
        }

        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();
        long durationMillis = outTimeMillis - inTimeMillis;

        if (durationMillis <= 0) {
            throw new IllegalArgumentException("Invalid Ticket: OutTime is before InTime");
        }

        int durationMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(durationMillis);

        // Calculate the fare based on the parking type
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                double fare = 0;
                if (durationMinutes <= Fare.FREE_DURATION_CAR) {
                    // Less than or equal to the free duration
                    fare = 0;
                } else {
                    // Apply regular rate
                    int billingMinutes = durationMinutes - Fare.FREE_DURATION_CAR + Fare.FREE_DURATION_CAR;
                    fare = billingMinutes * Fare.CAR_RATE_PER_MINUTE;
                }

                if (isReturningCustomer) {
                    // Apply returning customer discount
                    fare *= Fare.RETURNING_CUSTOMER_DISCOUNT;
                }

                ticket.setPrice(fare);
                break;
            }
            case BIKE: {
                double fare = 0;
                if (durationMinutes <= Fare.FREE_DURATION_BIKE) {
                    // Less than or equal to the free duration
                    fare = 0;
                } else {
                    // Apply regular rate
                    int billingMinutes = durationMinutes - Fare.FREE_DURATION_BIKE + Fare.FREE_DURATION_BIKE;
                    fare = billingMinutes * Fare.BIKE_RATE_PER_MINUTE;
                }

                if (isReturningCustomer) {
                    // Apply returning customer discount
                    fare *= Fare.RETURNING_CUSTOMER_DISCOUNT;
                }

                ticket.setPrice(fare);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}
