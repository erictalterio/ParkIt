package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAOTest {

    private TicketDAO ticketDAO;
    private DataBaseConfig dataBaseConfig;

    @BeforeEach
    public void setUp() {
        ticketDAO = new TicketDAO();
        dataBaseConfig = mock(DataBaseConfig.class);
        ticketDAO.dataBaseConfig = dataBaseConfig;
    }

    @Test
    public void saveTicketTest() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, null, false));
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(10.0);
        ticket.setInTime(new Timestamp(System.currentTimeMillis()));
        ticket.setOutTime(null);

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);

        // Act
        boolean isSaved = ticketDAO.saveTicket(ticket);

        // Assert
        assertTrue(isSaved);
        verify(dataBaseConfig).closeConnection(connection);
    }

    @Test
    public void getTicketTest() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        String vehicleRegNumber = "ABCDEF";

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.GET_TICKET)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);
        when(resultSet.getString(6)).thenReturn("CAR");

        // Act
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        // Assert
        assertNotNull(ticket);
        assertEquals(vehicleRegNumber, ticket.getVehicleRegNumber());
        verify(dataBaseConfig).closeResultSet(resultSet);
        verify(dataBaseConfig).closePreparedStatement(preparedStatement);
        verify(dataBaseConfig).closeConnection(connection);
    }

    @Test
    public void updateTicketTest() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setPrice(20.0);
        ticket.setOutTime(new Timestamp(System.currentTimeMillis()));

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);

        // Act
        boolean isUpdated = ticketDAO.updateTicket(ticket);

        // Assert
        assertTrue(isUpdated);
        verify(dataBaseConfig).closeConnection(connection);
    }
}
