package controller;

import common.DBConnector;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;


public class LoginController extends AbstractController {


    Locale location = Locale.getDefault();
    ResourceBundle myResources = ResourceBundle.getBundle("localization", location);
    Boolean databaseConnected = false;

    @FXML
    private TextField userIdField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label userIdLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label yourLocLabel;
    @FXML
    public Label apptLabel;
    @FXML
    public Label manageApptLabel;
    @FXML
    public Label schedulerApptLabel;

    @FXML
    void initialize() {
        System.out.println("Initialize login.");

        welcomeLabel.setText(myResources.getString("welcomeLabel"));
        userIdLabel.setText(myResources.getString("userIdLabel"));
        userIdField.setPromptText(myResources.getString("userPromptText"));
        passwordLabel.setText(myResources.getString("passwordLabel"));
        passwordField.setPromptText(myResources.getString("passwordPromptText"));
        loginButton.setText(myResources.getString("loginButton"));
        yourLocLabel.setText(myResources.getString("yourLocLabel"));
        apptLabel.setText(myResources.getString("apptLabel"));
        manageApptLabel.setText(myResources.getString("manageApptLabel"));
        schedulerApptLabel.setText(myResources.getString("schedulerApptLabel"));
        locationLabel.setText(AbstractController.getZoneId().getDisplayName(TextStyle.FULL, location));

        dbInit();
    }

    @FXML
    void loginHandler() throws IOException, SQLException {
        System.out.println("Attempting login...");
        String userId = userIdField.getText();
        String password = passwordField.getText();
        boolean successfulLogin = false;

        clearFields();

        if (databaseConnected && AbstractController.attemptLogin(userId, password)) {
            navigateTo("home");
            successfulLogin = true;
            System.out.println("Login successful.");
        } else {
            errorLabel.setText(myResources.getString("errorLabel"));
            System.out.println("Login failed.");
        }

        writeLoginActivityFile(userId, successfulLogin);
    }


    private void dbInit() {
        if (!databaseConnected) {
            if (DBConnector.connectToDB()) {
                databaseConnected = true;
            }
        }
    }

    private void clearFields() {
        userIdField.clear();
        passwordField.clear();
    }


    private void writeLoginActivityFile(String userId, boolean successfulLogin) {
        String loginState;
        try {
            File newFile = new File("login_activity.txt");
            if (newFile.createNewFile()) {
                System.out.println("New login activity file created.");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();

            if (successfulLogin) {
                loginState = "Successful";
            } else {
                loginState = "Failed";
            }

            FileWriter fWriter = new FileWriter(newFile, true);
            fWriter.write("\n" + formatter.format(currentTime) + " - " + loginState + " Login Attempt. Username: '" + userId + "'");
            fWriter.close();

        } catch (IOException e) {
            System.out.println("Login activity text file error: " + e.getMessage());
        }
    }
}
