package controller;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Country;
import model.Customer;
import model.FirstLevelDivision;
import util.Util;

import java.sql.SQLException;
import java.util.ArrayList;

public class ManageCustomerController {

    int numErrors;
    private ObservableList<String> countries = FXCollections.observableArrayList();
    private ObservableList<String> firstLvlDivs = FXCollections.observableArrayList();
    private boolean isUpdate;
    private Customer oldCustomer;
    private CustomerDAO customerDAO = new CustomerDAOImpl();
    private CountryDAO countryDAO = new CountryDAOImpl();
    private FirstLevelDivisionDAO firstLevelDivisionDAO = new FirstLevelDivisionDAOImpl();


    @FXML
    private TextField idField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneNumField;

    @FXML
    private ComboBox<String> countryComboBox;

    @FXML
    private ComboBox<String> firstLvlDivComboBox;

    @FXML
    private Text firstLvlDivText;

    @FXML
    private Label errorLabel;


    public void populateFields(Customer customer) {
        System.out.println("Selected Customer: " + customer.getName());
        idField.setText(Integer.toString(customer.getId()));
        nameField.setText(customer.getName());
        countryComboBox.setValue(customer.getCountry());
        firstLvlDivComboBox.setValue(customer.getFirstLvlDiv());
        addressField.setText(customer.getAddress());
        postalCodeField.setText(customer.getPostalCode());
        phoneNumField.setText(customer.getPhoneNum());

        isUpdate = true;
        oldCustomer = customer;
    }

    @FXML
    public void initialize() throws SQLException {
        ArrayList<Country> countries = countryDAO.getCountries();
        for (Country country : countries) {
            this.countries.add(country.getCountry());
        }

        countryComboBox.setItems(this.countries);
    }


    @FXML
    public void onSaveHandler() throws SQLException {
        if (isUpdate) {
            System.out.println("Update customer");
            if (fieldsValid()) {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String country = countryComboBox.getValue();
                String firstLvlDiv = firstLvlDivComboBox.getValue();
                String address = addressField.getText();
                String postalCode = postalCodeField.getText();
                String phoneNum = phoneNumField.getText();

                Customer newCustomer = new Customer(id, name, country, firstLvlDiv, address, postalCode, phoneNum);

                customerDAO.updateCustomer(oldCustomer, newCustomer);
                clearFields();
                errorLabel.setText("Customer updated: " + newCustomer.getName());
            } else if (numErrors > 1) {
                errorLabel.setText(errorLabel.getText() + " + " + (numErrors - 1) + " more errors.");
            }
        } else {
            System.out.println("Add customer");
            addCustomer();
        }
    }

    private void addCustomer() throws SQLException {
        if (idField.getText().isEmpty()) {
            if (fieldsValid()) {
                int id = Util.getNextCustomerId();
                String name = nameField.getText();
                String country = countryComboBox.getValue();
                String firstLvlDiv = firstLvlDivComboBox.getValue();
                String address = addressField.getText();
                String postalCode = postalCodeField.getText();
                String phoneNum = phoneNumField.getText();

                Customer newCustomer = new Customer(id, name, country, firstLvlDiv, address, postalCode, phoneNum);

                customerDAO.addCustomer(newCustomer);

                clearFields();
                errorLabel.setText("Customer added: " + newCustomer.getName());
            } else if (numErrors > 1) {
                errorLabel.setText(errorLabel.getText() + " + " + (numErrors - 1) + " more errors.");
            }
        } else {
            errorLabel.setText("Could not add customer; ID " + idField.getText() + " already exists.");
        }
    }


    @FXML
    void onCountryChangeHandler() throws SQLException {
        firstLvlDivComboBox.getItems().clear();
        firstLvlDivs.removeAll();
        if (countryComboBox.getValue() != null) {
            Country countryByName = countryDAO.getCountryByName(countryComboBox.getValue());

            if (countryByName != null) {
                int countryId = countryByName.getCountryId();
                ArrayList<FirstLevelDivision> firstLevelDivisionsByCountryId = firstLevelDivisionDAO.getFirstLevelDivisionsByCountryId(countryId);

                for (FirstLevelDivision firstLevelDivision : firstLevelDivisionsByCountryId) {
                    firstLvlDivs.add(firstLevelDivision.getDivision());
                }
                firstLvlDivComboBox.setItems(firstLvlDivs);
            } else {
                System.out.println("Query was not executed correctly.");
            }
        }
    }


    public boolean fieldsValid() {
        boolean value = true;
        String error = "";
        numErrors = 0;

        if (phoneNumField.getText().isEmpty()) {
            error = "Phone number field";
            numErrors++;
            value = false;
        }
        if (postalCodeField.getText().isEmpty()) {
            error = "Postal code field";
            numErrors++;
            value = false;
        }
        if (addressField.getText().isEmpty()) {
            error = "Address field";
            numErrors++;
            value = false;
        }
        if (firstLvlDivComboBox.getValue() == null) {
            error = firstLvlDivText.getText().replace(':', ' ');
            numErrors++;
            value = false;
        }
        if (countryComboBox.getValue() == null) {
            error = "Country box";
            numErrors++;
            value = false;
        }
        if (nameField.getText().isEmpty()) {
            error = "Name field";
            numErrors++;
            value = false;
        }

        if (numErrors == 1)
            errorLabel.setText(error + " is empty.");
        else if (numErrors > 1)
            errorLabel.setText(error + " is empty");
        else
            errorLabel.setText(error);
        return value;
    }


    @FXML
    void clearFields() {
        idField.clear();
        nameField.clear();
        countryComboBox.setValue(null);
        firstLvlDivComboBox.setValue(null);
        addressField.clear();
        postalCodeField.clear();
        phoneNumField.clear();
    }
}
