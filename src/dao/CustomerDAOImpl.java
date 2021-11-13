package dao;

import common.DBConnector;
import controller.AbstractController;
import model.Country;
import model.Customer;
import model.FirstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CustomerDAOImpl implements CustomerDAO {
    CountryDAO countryDAO = new CountryDAOImpl();
    FirstLevelDivisionDAO firstLevelDivisionDAO = new FirstLevelDivisionDAOImpl();

    /**
     * Returns all the customers
     *
     * @return
     */
    @Override
    public ArrayList<Customer> getCustomers() throws SQLException {
        ArrayList<Customer> customers = new ArrayList<>();
        PreparedStatement getCustomers = DBConnector.getDbConnection().prepareStatement("SELECT * FROM customers");
        ResultSet rs = getCustomers.executeQuery();

        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String address = rs.getString(3);
            String postCode = rs.getString(4);
            String pNum = rs.getString(5);

            FirstLevelDivision firstLevelDivision = firstLevelDivisionDAO.getFirstLevelDivisionById(rs.getInt(10));

            int countryId = firstLevelDivision.getCountryId();
            Country countryObj = countryDAO.getCountryById(countryId);

            Customer customer = new Customer(id, name, countryObj.getCountry(), firstLevelDivision.getDivision(), address, postCode, pNum);
            customers.add(customer);
        }
        return customers;
    }

    /**
     * Adds a new customer to customerList.
     *
     * @param customer the customer to add
     * @throws SQLException
     */
    public void addCustomer(Customer customer) throws SQLException {
        Customer.addCustomer(customer);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(formatter);

        ResultSet rs = DBConnector.executeQueryResults("SELECT Id FROM first_level_divisions WHERE division = '" + customer.getFirstLvlDiv() + "'");
        rs.next();
        DBConnector.executeUpdate("INSERT INTO customers VALUES('"
                + customer.getId() + "', '"
                + customer.getName() + "', '"
                + customer.getAddress() + "', '"
                + customer.getPostalCode() + "', '"
                + customer.getPhoneNum() + "', '"
                + curTime + "', '"
                + AbstractController.getLoggedInUser() + "', '"
                + curTime + "', '"
                + AbstractController.getLoggedInUser() + "', '"
                + rs.getInt(1) + "')");
    }


    /**
     * Replaces old customer in customerList with a new customer.
     *
     * @param oldCustomer the customer to be replaced
     * @param newCustomer the customer to replace oldCustomer
     * @throws SQLException
     */
    public void updateCustomer(Customer oldCustomer, Customer newCustomer) throws SQLException {
        Customer.updateCustomer(oldCustomer, newCustomer);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(formatter);

        ResultSet rs = DBConnector.executeQueryResults("SELECT Id FROM first_level_divisions WHERE division = '" + newCustomer.getFirstLvlDiv() + "'");
        rs.next();
        DBConnector.executeUpdate("UPDATE customers SET Name = '" + newCustomer.getName()
                + "', Address = '" + newCustomer.getAddress()
                + "', PostalCode = '" + newCustomer.getPostalCode()
                + "', Phone = '" + newCustomer.getPhoneNum()
                + "', LastUpdate = '" + curTime
                + "', LastUpdatedBy = '" + AbstractController.getLoggedInUser()
                + "', DivisionId = '" + rs.getInt(1) + "' WHERE Id = " + oldCustomer.getId());
    }

    /**
     * Removes a customer from customerList if it exists in the list.
     *
     * @param customer the customer to remove
     * @throws SQLException
     */
    public void deleteCustomer(Customer customer) throws SQLException {
        boolean deleted = Customer.deleteCustomer(customer);
        if (deleted) {
            DBConnector.executeUpdate("DELETE FROM appointments WHERE CustomerId = " + customer.getId());
            DBConnector.executeUpdate("DELETE FROM customers WHERE Id = " + customer.getId());
        }
    }
}
