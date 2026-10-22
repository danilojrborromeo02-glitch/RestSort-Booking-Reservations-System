package RestSort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomerStayManagement {

    private final String fileName =
            "C:\\Users\\Ailyn\\eclipse-workspace\\RESTSORT\\src\\RestSort\\customer_stays";

    private final DataManager data = new DataManager();
    private final ReservationManagement reservations =
            new ReservationManagement();

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public void timeIn(Scanner sc) {

        System.out.println("\n========== CUSTOMER TIME IN ==========");

        reservations.showReservations();

        System.out.print("\nEnter Reservation ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("Reservation ID is required.");
            return;
        }

        String[] reservation = reservations.getActiveReservationById(id);

        if (reservation == null || reservation.length < 7) {
            System.out.println("Active reservation not found.");
            return;
        }

        String status = reservation[6].trim();

        if (!status.equalsIgnoreCase("CONFIRMED")) {
            System.out.println("TIME IN DENIED.");
            System.out.println("Reservation must be fully paid and confirmed.");
            System.out.println("Current status: " + status);
            return;
        }

        String reservationDate = reservation[2].trim();

        try {
            LocalDate bookingDate = LocalDate.parse(reservationDate);

            if (!bookingDate.equals(LocalDate.now())) {
                System.out.println("TIME IN DENIED.");
                System.out.println("Reservation date: " + bookingDate);
                System.out.println("Today's date: " + LocalDate.now());
                System.out.println(
                        "Customer can only check in on the reservation date.");
                return;
            }

        } catch (DateTimeParseException e) {
            System.out.println("Invalid reservation date.");
            return;
        }

        ArrayList<String> records = data.readLines(fileName);

        for (String record : records) {
            String[] r = record.split(",", -1);

            if (r.length >= 6
                    && r[0].trim().equalsIgnoreCase(id)) {

                if (!r[3].trim().isEmpty()) {
                    System.out.println("Customer has already checked in.");
                    System.out.println("Time In: " + r[3]);
                    return;
                }
            }
        }

        String timeIn = LocalDateTime.now().format(formatter);

        String record = id + ","
                + clean(reservation[1]) + ","
                + reservationDate + ","
                + timeIn + ","
                + ","
                + "CHECKED IN";

        if (data.appendLine(fileName, record)) {
            System.out.println("\nCustomer checked in successfully.");
            System.out.println("Reservation ID: " + id);
            System.out.println("Customer: " + reservation[1]);
            System.out.println("Time In: " + timeIn);
            System.out.println("Stay Status: CHECKED IN");
        } else {
            System.out.println("Unable to save Time In record.");
        }
    }

    public void timeOut(Scanner sc) {

        System.out.println("\n========== CUSTOMER TIME OUT ==========");

        showStayRecords();

        System.out.print("\nEnter Reservation ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("Reservation ID is required.");
            return;
        }

        ArrayList<String> records = data.readLines(fileName);
        ArrayList<String> updated = new ArrayList<>();

        boolean found = false;
        boolean completed = false;

        for (String record : records) {

            String[] r = record.split(",", -1);

            if (r.length >= 6
                    && r[0].trim().equalsIgnoreCase(id)) {

                found = true;

                if (r[3].trim().isEmpty()) {
                    System.out.println(
                            "Cannot check out. Customer has not checked in.");
                    return;
                }

                if (!r[4].trim().isEmpty()
                        || r[5].trim().equalsIgnoreCase("COMPLETED")) {
                    System.out.println("Customer has already checked out.");
                    return;
                }

                r[4] = LocalDateTime.now().format(formatter);
                r[5] = "COMPLETED";

                updated.add(String.join(",", r));
                completed = true;

            } else {
                updated.add(record);
            }
        }

        if (!found) {
            System.out.println("Stay record not found.");
            System.out.println("Customer must check in first.");
            return;
        }

        if (completed && data.writeLines(fileName, updated)) {
            System.out.println("\nCustomer checked out successfully.");
            System.out.println("Reservation ID: " + id);
            System.out.println("Time Out: " +
                    findTimeOut(updated, id));
            System.out.println("Stay Status: COMPLETED");
        } else {
            System.out.println("Unable to save Time Out record.");
        }
    }

    public void showStayRecords() {

        ArrayList<String> records = data.readLines(fileName);

        System.out.println("\n========== CUSTOMER STAY RECORDS ==========");

        if (records.isEmpty()) {
            System.out.println("No stay records found.");
            return;
        }

        System.out.printf(
                "%-12s %-22s %-12s %-20s %-20s %-12s%n",
                "RESERVATION", "CUSTOMER", "DATE",
                "TIME IN", "TIME OUT", "STATUS");

        for (String record : records) {

            String[] r = record.split(",", -1);

            if (r.length >= 6) {
                System.out.printf(
                        "%-12s %-22s %-12s %-20s %-20s %-12s%n",
                        r[0], r[1], r[2], r[3],
                        r[4].isEmpty() ? "N/A" : r[4],
                        r[5]);
            }
        }
    }

    private String findTimeOut(
            ArrayList<String> records, String id) {

        for (String record : records) {
            String[] r = record.split(",", -1);

            if (r.length >= 6
                    && r[0].trim().equalsIgnoreCase(id)) {
                return r[4];
            }
        }

        return "";
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
AJ

package RestSort;

import java.util.Scanner;

public class AGENT {

    private final Scanner sc;
    private final String agentName;

    private final ReservationManagement reservations;
    private final AmmenityManagement amenities;
    private final CustomerManagement customers;
    private final TransactionManagement transactions;
    private final DiscountManagement discounts;

    public AGENT(Scanner sc, String agentName) {
        this.sc = sc;
        this.agentName = agentName;

        this.reservations = new ReservationManagement();
        this.amenities = new AmmenityManagement(sc);
        this.customers = new CustomerManagement();
        this.transactions = new TransactionManagement();
        this.discounts = new DiscountManagement(sc);
    }

    public void menu() {

        while (true) {

            System.out.println("\n========================================");
            System.out.println("             RESTSORT RESORT");
            System.out.println("              AGENT DASHBOARD");
            System.out.println("========================================");
            System.out.println("Welcome, " + agentName);
            System.out.println("----------------------------------------");
            System.out.println("[1] Customer Information");
            System.out.println("[2] View Amenities");
            System.out.println("[3] Create Reservation");
            System.out.println("[4] View Reservations");
            System.out.println("[5] Cancel Reservation");
            System.out.println("[6] Transactions / Billing");
            System.out.println("[7] Discount Management");
            System.out.println("[8] Manage Amenities");
            System.out.println("[9] Edit Reservation / Add Amenity");
            System.out.println("[10] Logout");
            System.out.println("========================================");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

            case "1":
                customers.menu(sc);
                break;

                case "2":
                    amenities.showAmenities();
                    break;

                case "3":
                    createReservation();
                    break;

                case "4":
                    reservations.showReservations();
                    break;

                case "5":
                    cancelReservation();
                    break;

                case "6":
                    transactions.menu(sc);
                    break;

                case "7":
                    discounts.menu();
                    break;

                case "8":
                    amenities.menu();
                    break;

                case "9":
                    editReservation();
                    break;

                case "10":
                    System.out.println("Logging out...");
                    return;

                default:
                    System.out.println(
                            "Invalid option. Please try again.");
            }
        }
    }


    private void createReservation() {

        System.out.println("\n========== CREATE RESERVATION ==========");

        System.out.print("Customer name: ");
        String customer = sc.nextLine().trim();

        if (customer.isEmpty()) {
            System.out.println("Customer name is required.");
            return;
        }

        System.out.print("Reservation date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();

        String amenity = amenities.selectAmenity(date);

        if (amenity == null) {
            System.out.println("Reservation cancelled.");
            return;
        }

        System.out.print("Additional details (or press Enter): ");
        String details = sc.nextLine().trim();

        boolean saved = reservations.addReservation(
                customer,
                date,
                amenity,
                details,
                agentName
        );

        if (saved) {
            System.out.println("Reservation created successfully.");
            System.out.println("Status: PENDING PAYMENT");
            System.out.println(
                    "Proceed to Transactions / Billing.");
        }
    }


    private void cancelReservation() {

        System.out.println("\n========== CANCEL RESERVATION ==========");

        reservations.showReservations();

        System.out.print("Enter Reservation ID to cancel: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("Reservation ID is required.");
            return;
        }

        reservations.cancelReservation(id);
    }


    private void editReservation() {

        System.out.println(
                "\n========== EDIT RESERVATION ==========");

        reservations.showReservations();

        System.out.print("Enter original Reservation ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("Reservation ID is required.");
            return;
        }

        String[] original =
                reservations.getActiveReservationById(id);

        if (original == null || original.length < 7) {
            System.out.println("Active reservation not found.");
            return;
        }

        System.out.println("\nReservation ID : " + original[0]);
        System.out.println("Customer       : " + original[1]);
        System.out.println("Date           : " + original[2]);
        System.out.println("Amenity        : " + original[3]);
        System.out.println("Status         : " + original[6]);

        if (original[6].trim().equalsIgnoreCase("CONFIRMED")) {
            System.out.println(
                    "Note: Adding an amenity may require updating the bill.");
        }

        System.out.println("\n[1] Add Another Amenity");
        System.out.println("[2] Return");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();

        if (!choice.equals("1")) {
            return;
        }

        String additionalAmenity =
                amenities.selectAmenity(original[2].trim());

        if (additionalAmenity == null) {
            System.out.println("Edit cancelled.");
            return;
        }

        System.out.print("Additional details (or press Enter): ");
        String details = sc.nextLine().trim();

        reservations.addAmenityToReservation(
                id,
                additionalAmenity,
                details,
                agentName
        );
    }
}