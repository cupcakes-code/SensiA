package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Customer {

    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();

    private int id;
    private String name;
    private String country;
    private String firstLvlDiv;
    private String address;
    private String postalCode;
    private String phoneNum;

    public Customer(int id,
                    String name,
                    String country,
                    String firstLvlDiv,
                    String address,
                    String postalCode,
                    String phoneNum) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.firstLvlDiv = firstLvlDiv;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getFirstLvlDiv() {
        return firstLvlDiv;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public static void addCustomer(Customer customer) {
        customerList.add(customer);
    }

    public static void updateCustomer(Customer oldCustomer, Customer newCustomer) {
        int index = customerList.indexOf(oldCustomer);
        customerList.set(index, newCustomer);
    }

    public static boolean deleteCustomer(Customer customer) {
        if (customerList.contains(customer)) {
            int index = customerList.indexOf(customer);
            customerList.remove(index);
            return true;
        } else {
            System.out.println("Couldn't delete customer; it doesn't exist in customerList.");
        }
        return false;
    }

    public static ObservableList<Customer> getAllCustomers() {
        return customerList;
    }

    public static void clearCustomers() {
        customerList.clear();
    }


}

