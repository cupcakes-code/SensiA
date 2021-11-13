package controller;

import common.DBConnector;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class HomeController extends AbstractController {
    @FXML
    public TableView<String> upcomingApptsTable;
    @FXML
    public TableColumn<String, String> upcomingAppointmentsCol;
    @FXML
    private Label alertLabel;


    @FXML
    void initialize() throws SQLException {
        loadData();
        LocalDateTime startTime;

        LocalDateTime curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss");

        ArrayList<String> upcomingAppointment = new ArrayList<>();
        for (Appointment a : Appointment.getAllAppointments()) {
            startTime = a.getStartDateTime();
            if (curTime.isBefore(startTime) && curTime.isAfter(startTime.minusMinutes(15))) {
                ZonedDateTime userZonedDateTime = startTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(AbstractController.getZoneId());
                upcomingAppointment.add(userZonedDateTime.format(formatter) + " - ID: " + a.getId());
            }
        }

        if (!upcomingAppointment.isEmpty()) {
            alertLabel.setText("Appointment within 15 minutes: ");
        } else {
            alertLabel.setText("No Appointment within 15 minutes");
        }

        ObservableList<String> upcominApptsList = FXCollections.observableArrayList(upcomingAppointment);
        upcomingAppointmentsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        upcomingApptsTable.setItems(upcominApptsList);
    }

    private void loadData() throws SQLException {
        AbstractController.populateAppointments();
        AbstractController.populateCustomers();
    }


    @FXML
    void viewApptsHandler() throws IOException {
        navigateTo("appointments");
    }


    @FXML
    void viewCustomersHandler() throws IOException {
        navigateTo("customers");
    }


    @FXML
    void logoutHandler() throws IOException, SQLException {
        navigateTo("login");
        Appointment.clearAppointments();
        Customer.clearCustomers();
        AbstractController.clearLoggedInUser();
        DBConnector.closeConnection();
    }

    @FXML
    void quitHandler() throws SQLException {
        System.out.println("Quitting application...");
        DBConnector.closeConnection();
        System.exit(0);
    }
}
