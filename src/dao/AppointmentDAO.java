package dao;

import model.Appointment;

import java.sql.SQLException;
import java.util.ArrayList;


public interface AppointmentDAO {

    /**
     * Returns all the appointments from the database
     * @return
     * @throws SQLException
     */
    ArrayList<Appointment> getAppointments() throws SQLException;

    /**
     * Adds a new appointment to appointmentList and database.
     *
     * @param appointment the appointment to add
     * @throws SQLException
     */
    void addAppointment(Appointment appointment) throws SQLException;

    /**
     * Replaces old appointment in appointmentList and database with a new appointment.
     *
     * @param oldAppointment the appointment to be replaced
     * @param newAppointment the appointment to replace oldCustomer
     * @throws SQLException
     */
    void updateAppointment(Appointment oldAppointment, Appointment newAppointment) throws SQLException;

    /**
     * Removes an appointment from appointmentList if it exists in the list.
     *
     * @param appointment the appointment to remove
     * @throws SQLException
     */
    void deleteAppointment(Appointment appointment) throws SQLException;

}
