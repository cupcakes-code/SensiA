package dao;

import common.DBConnector;
import model.FirstLevelDivision;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FirstLevelDivisionDAOImpl implements FirstLevelDivisionDAO {
    /**
     * @param firstLevelDivisionId
     * @return
     * @throws SQLException
     */
    @Override
    public FirstLevelDivision getFirstLevelDivisionById(int firstLevelDivisionId) throws SQLException {

        FirstLevelDivision firstLevelDivision = null;
        ResultSet divs = DBConnector.executeQueryResults("SELECT Id, Division, CountryId FROM first_level_divisions WHERE Id = " + firstLevelDivisionId);
        if (divs != null) {
            divs.next();
            firstLevelDivision = new FirstLevelDivision(divs.getInt(1), divs.getString(2), divs.getInt(3));
        }

        return firstLevelDivision;
    }

    /**
     * @param countryId
     * @return
     * @throws SQLException
     */
    @Override
    public ArrayList<FirstLevelDivision> getFirstLevelDivisionsByCountryId(int countryId) throws SQLException {
        ArrayList<FirstLevelDivision> firstLevelDivisions = new ArrayList<>();

        ResultSet divs = DBConnector.executeQueryResults("SELECT Id, Division, CountryId FROM first_level_divisions WHERE CountryId = " + countryId);
        if (divs != null) {
            divs.next();
            FirstLevelDivision firstLevelDivision = new FirstLevelDivision(divs.getInt(1), divs.getString(2), divs.getInt(3));
            firstLevelDivisions.add(firstLevelDivision);
        }
        return firstLevelDivisions;
    }
}
