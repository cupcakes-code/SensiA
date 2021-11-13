package controller;

import dao.CustomerDAO;
import dao.CustomerDAOImpl;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;

import java.io.IOException;
import java.sql.SQLException;


public class CustomersController extends AbstractController {
    private CustomerDAO customerDAO = new CustomerDAOImpl();

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> customerIdCol;

    @FXML
    private TableColumn<Customer, String> nameCol;

    @FXML
    private TableColumn<Customer, String> countryCol;

    @FXML
    private TableColumn<Customer, String> firstLvlDivCol;

    @FXML
    private TableColumn<Customer, String> addressCol;

    @FXML
    private TableColumn<Customer, String> postalCodeCol;

    @FXML
    private TableColumn<Customer, String> phoneNumCol;

    @FXML
    private Label errorLabel;


    @FXML
    void initialize() {
        System.out.println("Initialize customer records");

        configureTable();

        customerTable.setItems(Customer.getAllCustomers());


        customerTable.getSelectionModel().selectedItemProperty().addListener((newSelection) -> {
            if (newSelection != null && getSelection() != null) {
                errorLabel.setText("");
            } else {
                errorLabel.setText("");
            }
        });

        customerTable.setRowFactory(tableView -> {
            final TableRow<Customer> selectedCustomer = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem delete = new MenuItem("Delete Customer Record");
            final MenuItem update = new MenuItem("Update Customer Record");

            delete.setOnAction(event -> {
                System.out.println("On Delete Customer: " + selectedCustomer.getItem().getId());
                try {
                    onDeleteCustomerHandler();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            update.setOnAction(event -> {
                System.out.println("On Update Customer: " + selectedCustomer.getItem().getId());
                try {
                    onUpdateCustomerHandler();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            contextMenu.getItems().add(delete);
            contextMenu.getItems().add(update);

            selectedCustomer.contextMenuProperty().bind(Bindings.when(selectedCustomer.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return selectedCustomer;
        });
    }

    private void configureTable() {
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        firstLvlDivCol.setCellValueFactory(new PropertyValueFactory<>("firstLvlDiv"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneNumCol.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
    }


    Customer getSelection() {
        return customerTable.getSelectionModel().getSelectedItem();
    }


    @FXML
    void onUpdateCustomerHandler() throws IOException {
        System.out.println("Update customer");

        if (getSelection() != null) {
            manageCustomer(getSelection());
        } else {
            errorLabel.setText("Please select a customer to update.");
        }
    }

    public void manageCustomer(Customer customer) throws IOException {
        FXMLLoader loader = openDialogBox("managecustomer");
        ManageCustomerController controller = loader.getController();
        controller.populateFields(customer);
    }


    @FXML
    void onAddCustomerHandler() {
        System.out.println("Add customer");
        try {
            openDialogBox("managecustomer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onDeleteCustomerHandler() throws SQLException {
        System.out.println("Delete customer");

        if (getSelection() != null) {
            String name = getSelection().getName();
            int id = getSelection().getId();

            customerDAO.deleteCustomer(getSelection());
            customerTable.setItems(Customer.getAllCustomers());

            errorLabel.setText("Customer deleted: " + name);
            System.out.println("Deleted customer " + name + " with ID " + id);
        } else {
            errorLabel.setText("Please select a customer to delete.");
        }
    }


    @FXML
    void onBackButtonClickHandler() throws IOException {
        System.out.println("Home Page");
        navigateTo("home");
    }
}