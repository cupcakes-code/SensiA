package dao;

import common.DBConnector;

import model.Country;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CountryDAOImpl implements CountryDAO {
    /**
     * Returns all the countries from the database
     *
     * @throws SQLException
     */
    @Override
    public ArrayList<Country> getCountries() throws SQLException {
        ArrayList<Country> countries = new ArrayList<>();

        DBConnector.executeQuery("SELECT * FROM countries");
        while (DBConnector.getResults().next()) {
            countries.add(new Country(DBConnector.getResults().getInt(1), DBConnector.getResults().getString(2)));
        }

        return countries;
    }

    /**
     * Returns country by id from the database
     *
     * @param countryName country name to search
     * @throws SQLException
     */
    @Override
    public Country getCountryByName(String countryName) throws SQLException {
        Country country = null;
        ResultSet resultSet = DBConnector.executeQueryResults("SELECT * FROM countries WHERE Country = '" + countryName + "'");
        if (resultSet != null) {
            resultSet.next();
            country = new Country(resultSet.getInt(1), resultSet.getString(2));
        }

        return country;
    }

    /**
     * Returns country by id from the database
     *
     * @param countryId country id to search
     * @throws SQLException
     */
    @Override
    public Country getCountryById(int countryId) throws SQLException {
        Country country = null;
        ResultSet resultSet = DBConnector.executeQueryResults("SELECT * FROM countries WHERE Id = '" + countryId + "'");
        if (resultSet != null) {
            resultSet.next();
            country = new Country(resultSet.getInt(1), resultSet.getString(2));
        }
        return country;
    }
}
