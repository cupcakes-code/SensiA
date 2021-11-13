package dao;

import model.Contact;
import model.Country;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CountryDAO {
    /**
     * Returns all the contacts from the database
     *
     * @throws SQLException
     */
    ArrayList<Country> getCountries() throws SQLException;

    /**
     * Returns contact by id from the database
     *
     * @param contactName contact name to search
     * @throws SQLException
     */
    Country getCountryByName(String contactName) throws SQLException;

    /**
     * Returns contact by id from the database
     *
     * @param countryId contact id to search
     * @throws SQLException
     */
    Country getCountryById(int countryId) throws SQLException;
}
