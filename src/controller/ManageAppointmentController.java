package controller;

import common.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Appointment;
import model.Contact;
import util.Util;

import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ManageAppointmentController extends AbstractController {
    private String error = "";
    private ObservableList<String> contacts = FXCollections.observableArrayList();
    private Appointment oldAppointment;
    private boolean update;


    @FXML
    private TextField idField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField customerIdField;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField typeField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() throws SQLException {
        System.out.println("Initialize appointments");

        ArrayList<Contact> contacts = contactDAO.getContacts();
        for (Contact c : contacts) {
            this.contacts.add(c.getContactName());
        }
        contactComboBox.setItems(this.contacts);
    }



    public void populateFields(Appointment appointment) {
        System.out.println("Selected appointment: " + appointment.getTitle());
        idField.setText(Integer.toString(appointment.getId()));
        titleField.setText(appointment.getTitle());
        descriptionArea.setText(appointment.getDescription());
        locationField.setText(appointment.getLocation());
        contactComboBox.setValue(appointment.getContact());
        typeField.setText(appointment.getType());
        startDatePicker.setValue(appointment.getDisplayStartDate());
        endDatePicker.setValue(appointment.getDisplayEndDate());
        startTimeField.setText(appointment.getDisplayStartTime());
        endTimeField.setText(appointment.getDisplayEndTime());
        customerIdField.setText(Integer.toString(appointment.getCustomerId()));

        oldAppointment = appointment;
        update = true;
    }


    private void clearFields() {
        idField.clear();
        titleField.clear();
        descriptionArea.clear();
        locationField.clear();
        contactComboBox.setValue(null);
        typeField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        customerIdField.clear();
    }

    @FXML
    private void clearSelectionHandler() {
        clearFields();
    }


    @FXML
    private void onSaveHandler() throws SQLException {
        System.out.println("Add appointment");

        changeAppointments();
    }


    private void changeAppointments() throws SQLException {
        if (validateFields()) {
            int id = 0;
            if (!update) {
                id = Util.generateAppointmentId();
            } else {
                id = oldAppointment.getId();
            }

            String title = titleField.getText();
            String desc = descriptionArea.getText();
            String location = locationField.getText();
            String contact = contactComboBox.getValue();
            String type = typeField.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String startTimeStr = startTimeField.getText();
            String endTimeStr = endTimeField.getText();
            int customerId = Integer.parseInt(customerIdField.getText());
            int userId = AbstractController.getLoggedInUserId();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
            LocalTime endTime = LocalTime.parse(endTimeStr, formatter);
            ZonedDateTime utcStartDateTime = startDate.atTime(startTime).atZone(AbstractController.getZoneId()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime utcEndDateTime = endDate.atTime(endTime).atZone(AbstractController.getZoneId()).withZoneSameInstant(ZoneId.of("UTC"));

            Appointment newAppointment = new Appointment(id, title, desc, location, contact, type, utcStartDateTime.toLocalDateTime(), utcEndDateTime.toLocalDateTime(), customerId, userId);
            if (!update) {
                appointmentDAO.addAppointment(newAppointment);
                errorLabel.setText("Appointment added: " + newAppointment.getTitle());
            } else {
                appointmentDAO.updateAppointment(oldAppointment, newAppointment);
                errorLabel.setText("Appointment updated: " + newAppointment.getTitle());
            }
            clearFields();
        }
    }


    private boolean validateFields() throws SQLException {
        boolean rVal = true;
        boolean endTimeValid = false;
        boolean startTimeValid = false;
        boolean startDateExists = false;
        boolean endDateExists = false;
        error = "";
        int blanks = 0;

        if (customerIdField.getText().isEmpty()) {
            blanks++;
            rVal = false;
        } else if (!Util.containsOnlyDigits(customerIdField.getText())) {
            error = "Invalid customer ID; must be an integer.";
            rVal = false;
        } else {
            DBConnector.executeQuery("SELECT Customer_ID FROM customers WHERE Customer_ID = '" + customerIdField.getText() + "'");
            if (!DBConnector.getResults().next()) {
                error = "Customer ID does not exist in the database.";
                rVal = false;
            }
        }

        if (endTimeField.getText().isEmpty()) {
            blanks++;
            rVal = false;
        } else if (!validateTime(endDatePicker.getValue(), endTimeField.getText(), "end")) {
            rVal = false;
        } else {
            endTimeValid = true;
        }

        if (startTimeField.getText().isEmpty()) {
            blanks++;
            rVal = false;
        } else if (!validateTime(startDatePicker.getValue(), startTimeField.getText(), "start")) {
            rVal = false;
        } else {
            startTimeValid = true;
        }

        if (endDatePicker.getValue() == null) {
            blanks++;
            rVal = false;
        } else {
            endDateExists = true;
        }
        if (startDatePicker.getValue() == null) {
            blanks++;
            rVal = false;
        } else {
            startDateExists = true;
        }

        if (startTimeValid && endTimeValid && startDateExists && endDateExists) {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            LocalDateTime startDateTime = startDatePicker.getValue().atTime(LocalTime.parse(startTimeField.getText(), parser));
            LocalDateTime endDateTime = endDatePicker.getValue().atTime(LocalTime.parse(endTimeField.getText(), parser));

            if (startDateTime.compareTo(endDateTime) >= 0) {
                error = "Start date must be before end date.";
                rVal = false;
            } else {
                ZonedDateTime userStart = startDateTime.atZone(AbstractController.getZoneId()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime userEnd = endDateTime.atZone(AbstractController.getZoneId()).withZoneSameInstant(ZoneId.of("UTC"));

                if (!update) {
                    DBConnector.executeQuery("SELECT Start, End FROM appointments");
                } else {
                    DBConnector.executeQuery("SELECT Start, End FROM appointments WHERE Appointment_ID != " + oldAppointment.getId());
                }

                while (DBConnector.getResults().next()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(AbstractController.getZoneId().toString()));
                    ZonedDateTime dbStart = LocalDateTime.parse(DBConnector.getResults().getString(1), formatter).atZone(ZoneId.of("UTC"));
                    ZonedDateTime dbEnd = LocalDateTime.parse(DBConnector.getResults().getString(2), formatter).atZone(ZoneId.of("UTC"));

                    if (!userStart.isAfter(dbEnd) && !userEnd.isBefore(dbStart)) {
                        error = "Could not schedule appointment: Date overlaps another appointment.";
                        rVal = false;
                    }
                }
            }
        }

        if (typeField.getText().isEmpty()) {
            blanks++;
            rVal = false;
        }
        if (contactComboBox.getValue() == null) {
            blanks++;
            rVal = false;
        }
        if (locationField.getText().isEmpty()) {
            blanks++;
            rVal = false;
        }
        if (descriptionArea.getText().isEmpty()) {
            blanks++;
            rVal = false;
        }
        if (titleField.getText().isEmpty()) {
            blanks++;
            rVal = false;
        }

        if (blanks > 0) {
            errorLabel.setText("All fields required; " + blanks + " fields blank.");
        } else {
            errorLabel.setText(error);
        }

        return rVal;
    }



    private boolean validateTime(LocalDate date, String time, String endOrStart) {
        if (!time.matches("(([0-1][0-9]|[2][0-3])[:][0-5][0-9])|(([0-1][0-9]|[2][0-4])[:][0-5][0-9][:][0-5][0-9])")) {
            error = "Invalid " + endOrStart + " time: Must be formatted to HH:mm.";
            return false;
        } else if (date != null) {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm[:ss]");
            LocalTime locTime = LocalTime.parse(time, parser);
            ZonedDateTime zonedLocDateTime = date.atTime(locTime).atZone(AbstractController.getZoneId());
            ZonedDateTime estZonedDateTime = zonedLocDateTime.withZoneSameInstant(ZoneId.of("US/Eastern"));

            DayOfWeek day = estZonedDateTime.toLocalDateTime().getDayOfWeek();

            boolean tooEarly = estZonedDateTime.toLocalDateTime().isBefore(LocalDateTime.of(estZonedDateTime.toLocalDate(), LocalTime.of(8, 00)));
            boolean tooLate = estZonedDateTime.toLocalDateTime().isAfter(LocalDateTime.of(estZonedDateTime.toLocalDate(), LocalTime.of(22, 00)));
            boolean onWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;

            if (tooEarly || tooLate || onWeekend) {
                error = "Invalid appointment " + endOrStart + " date/time; Outside of EST business hours 8:00 - 22:00 weekdays. Date given: " + estZonedDateTime.toLocalDateTime().toString() + " EST";
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
