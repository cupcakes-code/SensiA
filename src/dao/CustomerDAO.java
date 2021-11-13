package dao;

import model.Customer;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CustomerDAO {

    /**
     * Returns all the customers
     * @return
     */
    ArrayList<Customer> getCustomers() throws SQLException;

    /**
     * Adds a new customer to customerList.
     *
     * @param customer the customer to add
     * @throws SQLException
     */
    void addCustomer(Customer customer) throws SQLException;


    /**
     * Replaces old customer in customerList with a new customer.
     *
     * @param oldCustomer the customer to be replaced
     * @param newCustomer the customer to replace oldCustomer
     * @throws SQLException
     */
    void updateCustomer(Customer oldCustomer, Customer newCustomer) throws SQLException;

    /**
     * Removes a customer from customerList if it exists in the list.
     *
     * @param customer the customer to remove
     * @throws SQLException
     */
    void deleteCustomer(Customer customer) throws SQLException;
}

