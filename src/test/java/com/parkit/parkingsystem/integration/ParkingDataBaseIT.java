package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingDataBaseIT {

    // Declare variables for ParkingSpotDAO and DataBaseConfig
    private static ParkingSpotDAO parkingSpotDAO;
    private static DataBaseConfig dataBaseConfig;

    // This method is executed before each test method
    @BeforeEach
    public static void setUp() {
        // Create a new instance of ParkingSpotDAO
        parkingSpotDAO = new ParkingSpotDAO();
        // Create a mock instance of DataBaseConfig
        dataBaseConfig = mock(DataBaseConfig.class);
        // Assign the mock instance to the dataBaseConfig field of ParkingSpotDAO
        parkingSpotDAO.dataBaseConfig = dataBaseConfig;
    }


    @AfterAll
    public static void tearDown() {
       
    }

    // Test method to verify the behavior of getNextAvailableSlot() method
    @Test
    public void getNextAvailableSlotTest() throws Exception {
        // Arrange
        // Create mock objects for Connection, PreparedStatement, and ResultSet
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        ParkingType parkingType = ParkingType.CAR;

        // Stub the behavior of the mock objects
        // - When getConnection() is called on dataBaseConfig, return the mock connection
        // - When prepareStatement() is called on the connection with the DBConstants.GET_NEXT_PARKING_SPOT query,
        //   return the mock prepared statement
        // - When executeQuery() is called on the prepared statement, return the mock result set
        // - When next() is called on the result set, return true
        // - When getInt(1) is called on the result set, return 1
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        // Act
        // Call the getNextAvailableSlot() method of parkingSpotDAO with the given parking type
        int availableSlot = parkingSpotDAO.getNextAvailableSlot(parkingType);

        // Assert
        // Verify that the available slot obtained is equal to 1
        assertEquals(1, availableSlot);
        // Verify that the closeResultSet() method is called on dataBaseConfig with the mock result set
        verify(dataBaseConfig).closeResultSet(resultSet);
        // Verify that the closePreparedStatement() method is called on dataBaseConfig with the mock prepared statement
        verify(dataBaseConfig).closePreparedStatement(preparedStatement);
        // Verify that the closeConnection() method is called on dataBaseConfig with the mock connection
        verify(dataBaseConfig).closeConnection(connection);
    }

    // Test method to verify the behavior of updateParking() method
    @Test
    public void updateParkingTest() throws Exception {
        // Arrange
        // Create mock objects for Connection and PreparedStatement
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        // Stub the behavior of the mock objects
        // - When getConnection() is called on dataBaseConfig, return the mock connection
        // - When prepareStatement() is called on the connection with the DBConstants.UPDATE_PARKING_SPOT query,
        //   return the mock prepared statement
        // - When executeUpdate() is called on the prepared statement, return 1
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        // Call the updateParking() method of parkingSpotDAO with the given parking spot
        boolean isUpdated = parkingSpotDAO.updateParking(parkingSpot);

        // Assert
        // Verify that the updateParking() method returns true
        assertTrue(isUpdated);
        // Verify that the closeConnection() method is called on dataBaseConfig with the mock connection
        verify(dataBaseConfig).closeConnection(connection);
    }
}
