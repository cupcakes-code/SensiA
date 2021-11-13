package controller;

import dao.AppointmentDAO;
import dao.AppointmentDAOImpl;
import dao.ContactDAO;
import dao.ContactDAOImpl;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Appointment;
import model.Contact;
import model.Customer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;


public class AppointmentsController extends AbstractController {
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private ContactDAO contactDAO = new ContactDAOImpl();
    private ObservableList<String> contacts = FXCollections.observableArrayList();
    private ObservableList<Integer> customerIds = FXCollections.observableArrayList();
    private ObservableList<Appointment> apptsToSet = FXCollections.observableArrayList();
    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, Integer> apptIdCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, String> descriptionCol;

    @FXML
    private TableColumn<Appointment, String> locationCol;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private TableColumn<Appointment, String> startDateTimeCol;

    @FXML
    private TableColumn<Appointment, String> endDateTimeCol;

    @FXML
    private TableColumn<Appointment, Integer> customerIdCol;

    @FXML
    private ComboBox<String> contactScheduleComboBox;

    @FXML
    private ComboBox<Integer> customerScheduleComboBox;

    @FXML
    private RadioButton monthRadioButton;

    @FXML
    private RadioButton weekRadioButton;

    @FXML
    private Label errorLabel;


    ObservableList<String> months = FXCollections.observableArrayList("January", "February", "March",
            "April", "May", "June", "July", "August", "September", "October", "November", "December");
    int numAppts = 0;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private Label numApptsLabel;


    @FXML
    void initialize() throws SQLException {
        System.out.println("Initialize appointments");

        apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("displayStartDateTime"));
        endDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("displayEndDateTime"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentTable.setItems(Appointment.getAllAppointments());

        ArrayList<Contact> contacts = contactDAO.getContacts();
        for (Contact c : contacts) {
            this.contacts.add(c.getContactName());
        }

        for (Customer c : Customer.getAllCustomers()) {
            customerIds.add(c.getId());
        }

        contactScheduleComboBox.setItems(this.contacts);
        customerScheduleComboBox.setItems(customerIds);
        weekRadioButton.setToggleGroup(toggleGroup);
        monthRadioButton.setToggleGroup(toggleGroup);

        appointmentTable.getSelectionModel().selectedItemProperty().addListener((newSelection) -> {
            if (newSelection != null && getSelection() != null) {
                errorLabel.setText("");
            } else {
                clearFields();
                errorLabel.setText("");
            }
        });


        appointmentTable.setRowFactory(tableView -> {
            final TableRow<Appointment> selectedCustomer = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem delete = new MenuItem("Delete Appointment");
            final MenuItem update = new MenuItem("Update Appointment");

            delete.setOnAction(event -> {
                System.out.println("On Delete Appointment: " + selectedCustomer.getItem().getId());
                try {
                    deleteHandler();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            update.setOnAction(event -> {
                System.out.println("On Update Appointment: " + selectedCustomer.getItem().getId());
                try {
                    updateHandler();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            contextMenu.getItems().add(delete);
            contextMenu.getItems().add(update);

            selectedCustomer.contextMenuProperty().bind(Bindings.when(selectedCustomer.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return selectedCustomer;
        });


        AbstractController.populateAppointments();
        ObservableList<String> types = FXCollections.observableArrayList();
        for (Appointment a : Appointment.getAllAppointments()) {
            numAppts++;
            if (!types.contains(a.getType())) {
                types.add(a.getType());
            }
        }

        numApptsLabel.setText(Integer.toString(numAppts));
        monthComboBox.setItems(months);
        typeComboBox.setItems(types);
    }


    public Appointment getSelection() {
        return appointmentTable.getSelectionModel().getSelectedItem();
    }


    public void clearFields() {
        if (getSelection() != null) {
            appointmentTable.getSelectionModel().clearSelection();
        }

        monthComboBox.setValue(null);
        typeComboBox.setValue(null);
        contactScheduleComboBox.setValue(null);
        customerScheduleComboBox.setValue(null);
        monthRadioButton.setSelected(false);
        weekRadioButton.setSelected(false);
        appointmentTable.setItems(Appointment.getAllAppointments());
    }


    @FXML
    public void sortByMonthHandler() {

        monthComboBox.setValue(null);
        typeComboBox.setValue(null);

        apptsToSet.clear();
        LocalDateTime curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getStartDateTime().getMonth() == curTime.getMonth()) {
                apptsToSet.add(a);
            }
        }
        appointmentTable.setItems(apptsToSet);
    }


    @FXML
    public void sortByWeekHandler() {

        monthComboBox.setValue(null);
        typeComboBox.setValue(null);

        apptsToSet.clear();
        LocalDateTime curTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime sunday = curTime.minusDays(curTime.getDayOfWeek().getValue());

        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getStartDateTime().isAfter(sunday) && a.getStartDateTime().isBefore(sunday.plusDays(6))) {
                apptsToSet.add(a);
            }
        }
        appointmentTable.setItems(apptsToSet);
    }


    @FXML
    public void contactScheduleHandler() {
        monthComboBox.setValue(null);
        typeComboBox.setValue(null);


        if (contactScheduleComboBox.getSelectionModel().getSelectedItem() != null) {
            apptsToSet.clear();
            for (Appointment a : Appointment.getAllAppointments()) {
                if (a.getContact().equals(contactScheduleComboBox.getSelectionModel().getSelectedItem())) {
                    apptsToSet.add(a);
                }
            }
            appointmentTable.setItems(apptsToSet);
        }
    }

    @FXML
    public void customerScheduleHandler() {
        monthComboBox.setValue(null);
        typeComboBox.setValue(null);
        if (customerScheduleComboBox.getSelectionModel().getSelectedItem() != null) {
            apptsToSet.clear();
            for (Appointment a : Appointment.getAllAppointments()) {
                if (a.getCustomerId() == customerScheduleComboBox.getSelectionModel().getSelectedItem()) {
                    apptsToSet.add(a);
                }
            }
            appointmentTable.setItems(apptsToSet);
        }
    }


    @FXML
    public void updateHandler() throws IOException {
        System.out.println("Update appointment");

        if (getSelection() != null) {
            manageAppointment(getSelection());
        } else {
            errorLabel.setText("Please select a appointment to update.");
        }
    }

    public void manageAppointment(Appointment appointment) throws IOException {
        FXMLLoader loader = openDialogBox("manageappointment");
        ManageAppointmentController controller = loader.getController();
        controller.populateFields(appointment);
    }


    @FXML
    public void onAddButtonClick() {
        System.out.println("Add appointment");

        System.out.println("Add appointment");
        try {
            openDialogBox("manageappointment");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void deleteHandler() throws SQLException {
        System.out.println("Delete appointment");
        if (getSelection() != null) {
            String id = Integer.toString(getSelection().getId());
            String type = getSelection().getType();

            appointmentDAO.deleteAppointment(getSelection());
            appointmentTable.setItems(Appointment.getAllAppointments());
            clearFields();

            errorLabel.setText("Appointment with ID " + id + " deleted: " + type);

        } else {
            errorLabel.setText("Please select an appointment to delete.");
        }
    }


    @FXML
    public void onBackButtonClick() throws IOException {
        System.out.println("Home Page");
        navigateTo("home");
    }


    @FXML
    public void clearSelectionHandler() {
        clearSelection();
    }


    public void clearSelection() {
        System.out.println("Clear selection");
        clearFields();
        if (getSelection() != null) {
            appointmentTable.getSelectionModel().clearSelection();
        }
        if (toggleGroup.getSelectedToggle() != null) {
            toggleGroup.getSelectedToggle().setSelected(false);
        }
    }


    @FXML
    public void onTypeChangeHandler() {
        filterAppontmentByMonthAndType();
    }


    @FXML
    public void onManthChangeHandler() {
        filterAppontmentByMonthAndType();
    }


    public void filterAppontmentByMonthAndType() {
        contactScheduleComboBox.setValue(null);
        customerScheduleComboBox.setValue(null);
        monthRadioButton.setSelected(false);
        weekRadioButton.setSelected(false);


        boolean typeExists = typeComboBox.getSelectionModel().getSelectedItem() != null;
        boolean monthExists = monthComboBox.getSelectionModel().getSelectedItem() != null;
        numAppts = 0;
        apptsToSet.clear();
        if (typeExists && monthExists) {
            numAppts = typeAndMonth();
        } else if (typeExists) {
            numAppts = typeOnly();
        } else if (monthExists) {
            numAppts = monthOnly();
        }
        appointmentTable.setItems(apptsToSet);
        numApptsLabel.setText(Integer.toString(numAppts));
    }


    public int typeAndMonth() {
        int i = 0;
        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getType().equals(typeComboBox.getSelectionModel().getSelectedItem()) && a.getStartDateTime().getMonthValue() - 1 == months.indexOf(monthComboBox.getSelectionModel().getSelectedItem())) {
                i++;
                apptsToSet.add(a);
            }
        }
        return i;
    }


    public int typeOnly() {
        int i = 0;
        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getType().equals(typeComboBox.getSelectionModel().getSelectedItem())) {
                i++;
                apptsToSet.add(a);
            }
        }
        return i;
    }


    public int monthOnly() {
        int i = 0;
        for (Appointment a : Appointment.getAllAppointments()) {
            if ((a.getStartDateTime().getMonthValue() - 1 == months.indexOf(monthComboBox.getSelectionModel().getSelectedItem()))) {
                i++;
                apptsToSet.add(a);
            }
        }
        return i;
    }

}




