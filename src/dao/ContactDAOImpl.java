package dao;

import common.DBConnector;
import model.Contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContactDAOImpl implements ContactDAO {
    /**
     * Returns all the contacts from the database
     *
     * @throws SQLException
     */
    @Override
    public ArrayList<Contact> getContacts() throws SQLException {
        ArrayList<Contact> contacts = new ArrayList<>();

        DBConnector.executeQuery("SELECT * FROM contacts");
        while (DBConnector.getResults().next()) {
            contacts.add(new Contact(DBConnector.getResults().getInt(1), DBConnector.getResults().getString(2)));
        }

        return contacts;
    }

    /**
     * Returns contact by id from the database
     *
     * @param contactName contact name to search
     * @throws SQLException
     */
    @Override
    public Contact getContactByName(String contactName) throws SQLException {
        Contact contact = null;
        ResultSet resultSet = DBConnector.executeQueryResults("SELECT * FROM contacts WHERE Name = '" + contactName + "'");
        if (resultSet != null) {
            resultSet.next();
            contact = new Contact(resultSet.getInt(1), resultSet.getString(2));
        }

        return contact;
    }

    /**
     * Returns contact by id from the database
     *
     * @param contactId contact id to search
     * @throws SQLException
     */
    @Override
    public Contact getContactById(String contactId) throws SQLException {
        Contact contact = null;
        ResultSet resultSet = DBConnector.executeQueryResults("SELECT * FROM contacts WHERE Id = '" + contactId + "'");
        if (resultSet != null) {
            resultSet.next();
            contact = new Contact(resultSet.getInt(1), resultSet.getString(2));
        }
        return contact;
    }
}
