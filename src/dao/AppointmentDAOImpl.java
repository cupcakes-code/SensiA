package dao;

import common.DBConnector;
import controller.AbstractController;
import model.Appointment;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AppointmentDAOImpl implements AppointmentDAO {

    private ContactDAO contactDAO = new ContactDAOImpl();

    /**
     * Returns all the appointments from the database
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<Appointment> getAppointments() throws SQLException {
        ArrayList<Appointment> appointments = new ArrayList<>();

        PreparedStatement preparedStatement = DBConnector.getDbConnection().prepareStatement("SELECT * FROM appointments");
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            int id = rs.getInt(1);
            String title = rs.getString(2);
            String description = rs.getString(3);
            String location = rs.getString(4);
            String type = rs.getString(5);
            int customerId = rs.getInt(12);
            int userId = rs.getInt(13);

            Contact contactObj = contactDAO.getContactById(rs.getString(14));
            String contact;
            if (contactObj != null) {
                contact = contactObj.getContactName();
            } else {
                contact = "NO CONTACT";
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(AbstractController.getZoneId().toString()));
            LocalDateTime startDateTime = LocalDateTime.parse(rs.getString(6), formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(rs.getString(7), formatter);

            Appointment appointment = new Appointment(id, title, description, location, contact, type, startDateTime, endDateTime, customerId, userId);
            appointments.add(appointment);
        }
        return appointments;
    }

    /**
     * Adds a new appointment to appointmentList and database.
     *
     * @param appointment the appointment to add
     * @throws SQLException
     */
    public void addAppointment(Appointment appointment) throws SQLException {
        Appointment.addAppointment(appointment);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sqlStartDateTime = appointment.getStartDateTime().format(formatter);
        String sqlEndDateTime = appointment.getEndDateTime().format(formatter);
        String curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(formatter);

        Contact contact = contactDAO.getContactByName(appointment.getContact());

        DBConnector.executeUpdate("INSERT INTO appointments VALUES('"
                + appointment.getId() + "', '"
                + appointment.getTitle() + "', '"
                + appointment.getDescription() + "', '"
                + appointment.getLocation() + "', '"
                + appointment.getType() + "', '"
                + sqlStartDateTime + "', '"
                + sqlEndDateTime + "', '"
                + curTime + "', '"
                + AbstractController.getLoggedInUser() + "', '"
                + curTime + "', '"
                + AbstractController.getLoggedInUser() + "', '"
                + appointment.getCustomerId() + "', '"
                + appointment.getUserId() + "', '"
                + contact.getContactId() + "')");

    }

    /**
     * Replaces old appointment in appointmentList and database with a new appointment.
     *
     * @param oldAppointment the appointment to be replaced
     * @param newAppointment the appointment to replace oldCustomer
     * @throws SQLException
     */
    public void updateAppointment(Appointment oldAppointment, Appointment newAppointment) throws SQLException {
        Appointment.updateAppointment(oldAppointment, newAppointment);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sqlStartDateTime = newAppointment.getStartDateTime().format(formatter);
        String sqlEndDateTime = newAppointment.getEndDateTime().format(formatter);
        String curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(formatter);

        Contact contact = contactDAO.getContactById(newAppointment.getContact());
        DBConnector.executeUpdate("UPDATE appointments SET Title = '" + newAppointment.getTitle()
                + "', Description = '" + newAppointment.getDescription()
                + "', Location = '" + newAppointment.getLocation()
                + "', Type = '" + newAppointment.getType()
                + "', Start = '" + sqlStartDateTime
                + "', End = '" + sqlEndDateTime
                + "', LastUpdate = '" + curTime
                + "', LastUpdated_By = '" + AbstractController.getLoggedInUser()
                + "', CustomerId = '" + newAppointment.getCustomerId()
                + "', UserId = '" + newAppointment.getUserId()
                + "', ContactId = '" + contact.getContactId() + "' WHERE Id = " + oldAppointment.getId());
    }

    /**
     * Removes an appointment from appointmentList if it exists in the list.
     *
     * @param appointment the appointment to remove
     * @throws SQLException
     */
    public void deleteAppointment(Appointment appointment) throws SQLException {
        boolean deleted = Appointment.deleteAppointment(appointment);
        if (deleted) {
            DBConnector.executeUpdate("DELETE FROM appointments WHERE Id = " + appointment.getId());
        }
    }

}
