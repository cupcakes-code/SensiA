package util;

import model.Appointment;
import model.Customer;

public class Util {
    public static boolean containsOnlyDigits(String s) {
        return s.matches("[0-9]+") ;
    }


    public static int generateAppointmentId() {
        int max = 0;
        for (Appointment a : Appointment.getAllAppointments()) {
            if (a.getId() > max) {
                max = a.getId();
            }
        }
        return max + 1;
    }

    public static int getNextCustomerId() {
        int max = 0;
        for (Customer c : Customer.getAllCustomers()) {
            if (c.getId() > max) {
                max = c.getId();
            }
        }
        return max + 1;
    }

}
