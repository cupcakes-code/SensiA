package dao;

import model.User;

import java.sql.SQLException;

public interface UserDAO {
    /**
     * Rreturns the user
     * @param userName
     * @param password
     * @return
     * @throws SQLException
     */
    User getUser(String userName, String password) throws SQLException;
}
