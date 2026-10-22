package RestSort;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class ReservationManagement {

    private final String fileName =
            "C:\\Users\\Ailyn\\eclipse-workspace\\RESTSORT\\src\\RestSort\\reservations";

    private final DataManager data = new DataManager();


    public boolean addReservation(
            String customer,
            String date,
            String amenity,
            String details,
            String agentName) {

        customer = clean(customer);
        date = clean(date);
        amenity = clean(amenity);
        details = clean(details);
        agentName = clean(agentName);

        if (customer.isEmpty() || date.isEmpty() || amenity.isEmpty()) {
            System.out.println("Customer, date, and amenity are required.");
            return false;
        }

        if (!isValidDate(date)) {
            System.out.println("Invalid date. Use YYYY-MM-DD.");
            return false;
        }

        if (!isAvailable(date, amenity)) {
            System.out.println(
                    "The selected amenity is already reserved on " + date + ".");
            return false;
        }

        ArrayList<String> records = data.readLines(fileName);
        String id = generateReservationId(records);

  
        String record = id + ","
                + customer + ","
                + date + ","
                + amenity + ","
                + details + ","
                + agentName + ","
                + "PENDING PAYMENT";

        if (data.appendLine(fileName, record)) {
            System.out.println("\nReservation saved successfully.");
            System.out.println("Reservation ID: " + id);
            System.out.println("Reservation Status: PENDING PAYMENT");
            return true;
        }

        System.out.println("Failed to save reservation.");
        return false;
    }

 
    public boolean isAvailable(String date, String amenity) {

        date = clean(date);
        amenity = clean(amenity);

        if (date.isEmpty() || amenity.isEmpty()) {
            return false;
        }

        ArrayList<String> records = data.readLines(fileName);

        for (String record : records) {

            if (record == null || record.trim().isEmpty()) {
                continue;
            }

            String[] r = record.split(",", -1);

         
            if (r.length >= 7) {

                String savedDate = r[2].trim();
                String savedAmenities = r[3].trim();
                String status = r[6].trim();

                if (!savedDate.equals(date)
                        || status.equalsIgnoreCase("CANCELLED")) {
                    continue;
                }

                if (containsAmenity(savedAmenities, amenity)) {
                    return false;
                }
            }

       
            else if (record.contains("|")) {

                String[] rLegacy = record.split("\\|", -1);

                if (rLegacy.length >= 6) {

                    String savedAmenity = rLegacy[2].trim();
                    String savedDate = rLegacy[3].trim();
                    String status = rLegacy[5].trim();

                    if (savedDate.equals(date)
                            && !status.equalsIgnoreCase("CANCELLED")
                            && containsAmenity(savedAmenity, amenity)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean hasAnyReservationOnDate(String date) {

        ArrayList<String> records = data.readLines(fileName);

        for (String record : records) {

            if (record == null || record.trim().isEmpty()) {
                continue;
            }

            String[] r = record.split(",", -1);

            if (r.length >= 7
                    && r[2].trim().equals(date.trim())
                    && !r[6].trim().equalsIgnoreCase("CANCELLED")) {
                return true;
            }

            if (record.contains("|")) {

                String[] legacy = record.split("\\|", -1);

                if (legacy.length >= 6
                        && legacy[3].trim().equals(date.trim())
                        && !legacy[5].trim().equalsIgnoreCase("CANCELLED")) {
                    return true;
                }
            }
        }

        return false;
    }

    public void showReservations() {

        ArrayList<String> records = data.readLines(fileName);

        System.out.println("\n========== RESERVATION RECORDS ==========");

        if (records.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        boolean found = false;

        for (String record : records) {

            if (record == null || record.trim().isEmpty()) {
                continue;
            }

            String[] r = record.split(",", -1);

            if (r.length >= 7) {

                found = true;

                System.out.println("----------------------------------------");
                System.out.println("Reservation ID : " + r[0]);
                System.out.println("Customer       : " + r[1]);
                System.out.println("Date           : " + r[2]);
                System.out.println("Amenity        : " + r[3]);
                System.out.println("Details        : " + r[4]);
                System.out.println("Agent          : " + r[5]);
                System.out.println("Status         : " + r[6]);
                

            } else if (record.contains("|")) {

                found = true;

                System.out.println("----------------------------------------");
                System.out.println("Legacy reservation record:");
                System.out.println(record);
            }
        }

        if (!found) {
            System.out.println("No valid reservations found.");
        }

        System.out.println("----------------------------------------");
    }

    public void showReservationsForAmenity(
            String date,
            String amenity) {

        ArrayList<String> records = data.readLines(fileName);

        System.out.println("\n========== AMENITY RESERVATIONS ==========");
        System.out.println("Date    : " + date);
        System.out.println("Amenity : " + amenity);

        boolean found = false;

        for (String record : records) {

            if (record == null || record.trim().isEmpty()) {
                continue;
            }

            String[] r = record.split(",", -1);

            if (r.length >= 7) {

                if (r[2].trim().equals(date.trim())
                        && !r[6].trim().equalsIgnoreCase("CANCELLED")
                        && containsAmenity(r[3], amenity)) {

                    found = true;

                    System.out.println("----------------------------------------");
                    System.out.println("Reservation ID: " + r[0]);
                    System.out.println("Customer      : " + r[1]);
                    System.out.println("Date          : " + r[2]);
                    System.out.println("Amenity       : " + r[3]);
                    System.out.println("Status        : " + r[6]);
                }
            }
        }

        if (!found) {
            System.out.println("No reservations found for this amenity and date.");
        }

        System.out.println("----------------------------------------");
    }


    public void showReservationsByDate(String date) {

        ArrayList<String> records = data.readLines(fileName);

        System.out.println("\n========== RESERVATIONS BY DATE ==========");
        System.out.println("Date: " + date);

        boolean found = false;

        for (String record : records) {

            if (record == null || record.trim().isEmpty()) {
                continue;
            }

            String[] r = record.split(",", -1);

            if (r.length >= 7
                    && r[2].trim().equals(date.trim())
                    && !r[6].trim().equalsIgnoreCase("CANCELLED")) {

                found = true;

                System.out.println("----------------------------------------");
                System.out.println("Reservation ID : " + r[0]);
                System.out.println("Customer       : " + r[1]);
                System.out.println("Amenity        : " + r[3]);
                System.out.println("Details        : " + r[4]);
                System.out.println("Agent          : " + r[5]);
                System.out.println("Status         : " + r[6]);
            }
        }

        if (!found) {
            System.out.println("No active reservations found for this date.");
        }

        System.out.println("----------------------------------------");
    }


    public String[] getActiveReservationById(String reservationId) {

        ArrayList<String> records = data.readLines(fileName);

        for (String record : records) {

            if (record == null || record.trim().isEmpty()
                    || !record.contains(",")) {
                continue;
            }

            String[] r = record.split(",", -1);

            if (r.length >= 7
                    && r[0].trim().equalsIgnoreCase(reservationId.trim())
                    && !r[6].trim().equalsIgnoreCase("CANCELLED")) {
                return r;
            }
        }

        return null;
    }

   
    public void cancelReservation(String reservationId) {

        ArrayList<String> records = data.readLines(fileName);
        ArrayList<String> updatedRecords = new ArrayList<>();

        boolean found = false;

        for (String record : records) {

            String[] r = record.split(",", -1);

            if (r.length >= 7
                    && r[0].trim().equalsIgnoreCase(reservationId.trim())) {

                if (r[6].trim().equalsIgnoreCase("CANCELLED")) {
                    System.out.println("This reservation is already cancelled.");
                    return;
                }

                r[6] = "CANCELLED";
                updatedRecords.add(String.join(",", r));
                found = true;

            } else {
                updatedRecords.add(record);
            }
        }

        if (!found) {
            System.out.println("Reservation ID not found.");
            return;
        }

        if (data.writeLines(fileName, updatedRecords)) {
            System.out.println("Reservation cancelled successfully.");
        } else {
            System.out.println("Failed to cancel reservation.");
        }
    }


    public boolean addAmenityToReservation(
            String reservationId,
            String additionalAmenity,
            String additionalDetails,
            String agentName) {

        String[] original = getActiveReservationById(reservationId);

        if (original == null || original.length < 7) {
            System.out.println("Active reservation not found.");
            return false;
        }

        if (original[6].trim().equalsIgnoreCase("CANCELLED")) {
            System.out.println("Cannot edit a cancelled reservation.");
            return false;
        }

        String date = original[2].trim();
        String oldAmenity = original[3].trim();
        String newAmenity = clean(additionalAmenity);

        if (newAmenity.isEmpty()) {
            System.out.println("Amenity is required.");
            return false;
        }

        if (containsAmenity(oldAmenity, newAmenity)) {
            System.out.println(
                    "This amenity is already included in the reservation.");
            return false;
        }

     
        ArrayList<String> records = data.readLines(fileName);

        for (String record : records) {

            if (record == null || record.trim().isEmpty()) {
                continue;
            }

            String[] r = record.split(",", -1);

            if (r.length < 7) {
                continue;
            }

            if (r[0].trim().equalsIgnoreCase(reservationId.trim())) {
                continue;
            }

            if (!r[2].trim().equals(date)) {
                continue;
            }

            if (r[6].trim().equalsIgnoreCase("CANCELLED")) {
                continue;
            }

            if (containsAmenity(r[3], newAmenity)) {
                System.out.println(
                        "This amenity is already booked on " + date + ".");
                return false;
            }
        }

        ArrayList<String> updatedRecords = new ArrayList<>();
        boolean found = false;

        for (String record : records) {

            String[] r = record.split(",", -1);

            if (r.length >= 7
                    && r[0].trim().equalsIgnoreCase(reservationId.trim())) {

                if (r[6].trim().equalsIgnoreCase("CANCELLED")) {
                    System.out.println("Cannot edit a cancelled reservation.");
                    return false;
                }

                r[3] = r[3].trim() + " + " + newAmenity;

                String details = clean(additionalDetails);

                if (!details.isEmpty()) {
                    if (r[4].trim().isEmpty()) {
                        r[4] = "Additional amenity: " + details;
                    } else {
                        r[4] = r[4] + " | Additional amenity: " + details;
                    }
                }

                updatedRecords.add(String.join(",", r));
                found = true;

            } else {
                updatedRecords.add(record);
            }
        }

        if (!found) {
            System.out.println("Reservation not found.");
            return false;
        }

        if (data.writeLines(fileName, updatedRecords)) {
            System.out.println("Reservation updated successfully.");
            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Added Amenity: " + newAmenity);
            System.out.println(
                    "Please review the bill for the additional amenity.");
            return true;
        }

        System.out.println("Failed to update reservation.");
        return false;
    }


    public void markReservationPaid(String reservationId) {

        ArrayList<String> records = data.readLines(fileName);
        ArrayList<String> updatedRecords = new ArrayList<>();

        boolean found = false;

        for (String record : records) {

            String[] r = record.split(",", -1);

            if (r.length >= 7
                    && r[0].trim().equalsIgnoreCase(reservationId.trim())) {

                if (r[6].trim().equalsIgnoreCase("CANCELLED")) {
                    System.out.println(
                            "Cancelled reservation cannot be confirmed.");
                    return;
                }

                r[6] = "CONFIRMED";
                updatedRecords.add(String.join(",", r));
                found = true;

            } else {
                updatedRecords.add(record);
            }
        }

        if (!found) {
            System.out.println("Reservation not found. Check the Reservation ID.");
            return;
        }

        if (data.writeLines(fileName, updatedRecords)) {
            System.out.println("Reservation status updated to CONFIRMED.");
        } else {
            System.out.println("Failed to update reservation status.");
        }
    }


    private String generateReservationId(ArrayList<String> records) {

        int highest = 0;

        for (String record : records) {

            String[] r = record.split(",", -1);

            if (r.length > 0
                    && r[0].trim().toUpperCase().startsWith("RS")) {

                try {
                    int number = Integer.parseInt(
                            r[0].trim().substring(2));

                    if (number > highest) {
                        highest = number;
                    }

                } catch (NumberFormatException ignored) {
            
                }
            }
        }

        return "RS" + (highest + 1);
    }


    private boolean containsAmenity(String existing, String target) {

        if (existing == null || target == null) {
            return false;
        }

        String[] names = existing.split("\\s*\\+\\s*");

        for (String name : names) {
            if (name.trim().equalsIgnoreCase(target.trim())) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidDate(String date) {

        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value.replace(",", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }
}