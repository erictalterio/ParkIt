package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    private static final Logger logger = LogManager.getLogger("CustomerDAO");

    private DataBaseConfig dataBaseConfig;

    public CustomerDAO() {
        this.dataBaseConfig = new DataBaseConfig();
    }

    public boolean isReturningCustomer(String vehicleRegNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean isReturningCustomer = false;

        try {
            connection = dataBaseConfig.getConnection();
            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM ticket WHERE VEHICLE_REG_NUMBER = ?");
            preparedStatement.setString(1, vehicleRegNumber);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                isReturningCustomer = count > 0;
            }
        } catch (Exception e) {
            logger.error("Error checking if customer is returning", e);
        } finally {
            dataBaseConfig.closeResultSet(resultSet);
            dataBaseConfig.closePreparedStatement(preparedStatement);
            dataBaseConfig.closeConnection(connection);
        }

        return isReturningCustomer;
    }

    public boolean isReturningCustomerOut(String vehicleRegNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean isReturningCustomerOut = false;

        try {
            connection = dataBaseConfig.getConnection();
            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM ticket WHERE VEHICLE_REG_NUMBER = ?");
            preparedStatement.setString(1, vehicleRegNumber);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                isReturningCustomerOut = count > 1;
            }
        } catch (Exception e) {
            logger.error("Error checking if customer is returning", e);
        } finally {
            dataBaseConfig.closeResultSet(resultSet);
            dataBaseConfig.closePreparedStatement(preparedStatement);
            dataBaseConfig.closeConnection(connection);
        }

        return isReturningCustomerOut;
    }

}
