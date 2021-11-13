package model;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import controller.AbstractController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Appointment {
    

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    
    private int id;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int customerId;
    private int userId;
    
    private String displayStartDateTime;
    private String displayEndDateTime;
    

    public Appointment(int id, 
                       String title, 
                       String description, 
                       String location, 
                       String contact, 
                       String type, 
                       LocalDateTime startDateTime, 
                       LocalDateTime endDateTime,
                       int customerId,
                       int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.customerId = customerId;
        this.userId = userId;
        
        
        ZonedDateTime zonedStartDateTime = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedEndDateTime = endDateTime.atZone(ZoneId.of("UTC"));
        
        ZonedDateTime userStartDateTime = zonedStartDateTime.withZoneSameInstant(AbstractController.getZoneId());
        ZonedDateTime userEndDateTime = zonedEndDateTime.withZoneSameInstant(AbstractController.getZoneId());
        
        displayStartDateTime = userStartDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        displayEndDateTime = userEndDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getDisplayStartDateTime() {
        return displayStartDateTime;
    }
    

    public String getDisplayEndDateTime() {
        return displayEndDateTime;
    }

    public LocalDate getDisplayStartDate() {
        ZonedDateTime zonedStartDate = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userStartDate = zonedStartDate.withZoneSameInstant(AbstractController.getZoneId());
        return userStartDate.toLocalDate();
    }

    public LocalDate getDisplayEndDate() {
        ZonedDateTime zonedEndDate = endDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userEndDate = zonedEndDate.withZoneSameInstant(AbstractController.getZoneId());
        return userEndDate.toLocalDate();
    }

    public String getDisplayStartTime() {
        ZonedDateTime zonedStartTime = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userStartTime = zonedStartTime.withZoneSameInstant(AbstractController.getZoneId());
        String displayStartTime = userStartTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return displayStartTime;
    }

    public String getDisplayEndTime() {
        ZonedDateTime zonedEndTime = endDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime userEndTime = zonedEndTime.withZoneSameInstant(AbstractController.getZoneId());
        String displayEndTime = userEndTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return displayEndTime;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getUserId() {
        return userId;
    }

    public static void addAppointment(Appointment appointment)  {
        appointmentList.add(appointment);
    }
    

    public static void updateAppointment(Appointment oldAppointment, Appointment newAppointment) throws SQLException {
        int index = appointmentList.indexOf(oldAppointment);
        appointmentList.set(index, newAppointment);
    }
    

    public static boolean deleteAppointment(Appointment appointment) throws SQLException {
        if (appointmentList.contains(appointment)) {
            int index = appointmentList.indexOf(appointment);

            appointmentList.remove(index);

            return true;
        } else {
            System.out.println("Couldn't delete appointment; it doesn't exist in appointmentList.");
        }
        return false;
    }



    public static ObservableList<Appointment> getAllAppointments() {
        return appointmentList;
    }

    public static void clearAppointments() {
        appointmentList.clear();
    }
}
