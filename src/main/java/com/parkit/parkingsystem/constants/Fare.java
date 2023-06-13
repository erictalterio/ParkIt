package com.parkit.parkingsystem.constants;

public final class Fare {
    public static final double CAR_RATE_PER_MINUTE = 0.025;
    public static final double BIKE_RATE_PER_MINUTE = 0.016;
    public static final double RETURNING_CUSTOMER_DISCOUNT = 0.95;
    public static final int FREE_DURATION_CAR = 30; // 30 minutes
    public static final int FREE_DURATION_BIKE = 30; // 30 minutes

    private Fare() {
        throw new IllegalStateException("Utility class");
    }
}
