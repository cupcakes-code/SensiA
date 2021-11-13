package dao;

import common.DBConnector;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {

    /**
     * Returns the user
     * @param userName
     * @param password
     * @return
     * @throws SQLException
     */
    @Override
    public User getUser(String userName, String password) throws SQLException {
        User user = null;
        ResultSet resultSet = DBConnector.executeQueryResults("SELECT Username, Password, Id FROM users WHERE Username = '" + userName + "' AND Password = '" + password + "'");
        if(resultSet != null) {
            resultSet.next();
            user = new User(resultSet.getInt(3), resultSet.getString(1), resultSet.getString(2));
        }

        return user;
    }
}
