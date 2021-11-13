package dao;

import model.Contact;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ContactDAO {
    /**
     * Returns all the contacts from the database
     *
     * @throws SQLException
     */
    ArrayList<Contact> getContacts() throws SQLException;

    /**
     * Returns contact by id from the database
     *
     * @param contactName contact name to search
     * @throws SQLException
     */
    Contact getContactByName(String contactName) throws SQLException;

    /**
     * Returns contact by id from the database
     *
     * @param contactId contact id to search
     * @throws SQLException
     */
    Contact getContactById(String contactId) throws SQLException;
}
