package controller;

import dao.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Appointment;
import model.Customer;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;

public class AbstractController {

    protected AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    protected ContactDAO contactDAO = new ContactDAOImpl();
    private static Scene scene;
    private static Stage stage;
    private static ZoneId zoneId = ZoneId.systemDefault();
    private static String loggedInUser;
    private static int loggedInUserId;

    public static void start(Stage startStage) throws IOException {
        AbstractController controller = new AbstractController();
        stage = startStage;
        stage.setTitle("Scheduling Application");
        controller.navigateTo("login");
    }

    public void navigateTo(String fxml) throws IOException {
        scene = new Scene(loadFXML(fxml));
        stage.setScene(scene);
        stage.show();
    }

    public Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AbstractController.class.getResource("/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public FXMLLoader openDialogBox(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(AbstractController.class.getResource("/view/" + fxml + ".fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(loader.load()));

        stage.show();
        return loader;
    }

    public static ZoneId getZoneId() {
        return zoneId;
    }

    public static boolean attemptLogin(String userName, String password) throws SQLException {
        UserDAO userDAO = new UserDAOImpl();
        User user = userDAO.getUser(userName, password);
        if (user != null) {
            loggedInUser = userName;
            loggedInUserId = user.getUserId();
            return true;
        }
        return false;
    }

    public static void populateCustomers() throws SQLException {
        System.out.println("Obtaining customer data from database...");
        Customer.clearCustomers();
        CustomerDAO customerDAO = new CustomerDAOImpl();
        ArrayList<Customer> customers = customerDAO.getCustomers();
        for (Customer customer : customers) {
            Customer.addCustomer(customer);
        }

        System.out.println("Successfully obtained customer data.");
    }

    public static void populateAppointments() throws SQLException {
        System.out.println("Obtaining appointment data from database...");
        Appointment.clearAppointments();
        AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
        ArrayList<Appointment> appointments = appointmentDAO.getAppointments();
        for (Appointment appointment : appointments) {
            Appointment.addAppointment(appointment);
        }
        System.out.println("Successfully obtained appointment data.");
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void clearLoggedInUser() {
        loggedInUser = null;
        loggedInUserId = -1;
    }
}
