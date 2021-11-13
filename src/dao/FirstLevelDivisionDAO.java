package dao;

import model.FirstLevelDivision;

import java.sql.SQLException;
import java.util.ArrayList;

public interface FirstLevelDivisionDAO {
    /**
     *
     * @param firstLevelDivisionId
     * @return
     * @throws SQLException
     */
    FirstLevelDivision getFirstLevelDivisionById(int firstLevelDivisionId) throws SQLException;


    /**
     *
     * @param countryId
     * @return
     * @throws SQLException
     */
    ArrayList<FirstLevelDivision> getFirstLevelDivisionsByCountryId(int countryId) throws SQLException;
}
