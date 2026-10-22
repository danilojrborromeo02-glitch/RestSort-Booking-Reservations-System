package RestSort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class TransactionManagement {

    private final String transactionFile =
            "C:\\Users\\Ailyn\\eclipse-workspace\\RESTSORT\\src\\RestSort\\transaction";

    private final String amenityFile =
            "C:\\Users\\Ailyn\\eclipse-workspace\\RESTSORT\\src\\RestSort\\amenities";

    private final String stayFile =
            "C:\\Users\\Ailyn\\eclipse-workspace\\RESTSORT\\src\\RestSort\\customer_stays";

    private final DataManager data = new DataManager();
    private final ReservationManagement reservations =
            new ReservationManagement();

    private final DateTimeFormatter dateTimeFormat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public void menu() {
        menu(new Scanner(System.in));
    }

    public void menu(Scanner sc) {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("         TRANSACTIONS / BILLING");
            System.out.println("======================================");
            System.out.println("[1] Create Bill");
            System.out.println("[2] Record Payment");
            System.out.println("[3] Print Receipt");
            System.out.println("[4] View Billing Records");
            System.out.println("[5] Check Reservation Balance");
            System.out.println("[6] Customer Time In");
            System.out.println("[7] Customer Time Out");
            System.out.println("[8] View Stay Records");
            System.out.println("[9] Back to Agent Dashboard");
            System.out.println("======================================");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    createBill(sc);
                    break;
                case "2":
                    recordPayment(sc);
                    break;
                case "3":
                    printReceipt(sc);
                    break;
                case "4":
                    showTransactions();
                    break;
                case "5":
                    checkBalance(sc);
                    break;
                case "6":
                    timeIn(sc);
                    break;
                case "7":
                    timeOut(sc);
                    break;
                case "8":
                    showStayRecords();
                    break;
                case "9":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void createBill(Scanner sc) {
        System.out.println("\n========== CREATE BILL ==========");

        reservations.showReservations();

        System.out.print("\nEnter Reservation ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("Reservation ID cannot be empty.");
            return;
        }

        String[] reservation =
                reservations.getActiveReservationById(id);

        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }

        if (reservation.length < 7) {
            System.out.println("Invalid reservation record.");
            return;
        }

        if (reservation[6].trim().equalsIgnoreCase("CANCELLED")) {
            System.out.println(
                    "Cannot create a bill for a cancelled reservation.");
            return;
        }

        ArrayList<String> transactions =
                data.readLines(transactionFile);

        if (findTransactionIndex(transactions, id) >= 0) {
            System.out.println(
                    "A bill already exists for this reservation.");
            System.out.println(
                    "Use Record Payment or Check Reservation Balance.");
            return;
        }

        double total = calculateAmenityTotal(reservation[3]);

        if (total < 0) {
            System.out.println("Unable to calculate the amenity price.");
            System.out.println("Please check the amenities file.");
            return;
        }

        String bill = clean(id) + ","
                + clean(reservation[1]) + ","
                + total + ",0.0,UNPAID";

        if (data.appendLine(transactionFile, bill)) {
            System.out.println("\nBill created successfully!");
            System.out.println("--------------------------------------");
            System.out.println("Reservation ID : " + id);
            System.out.println("Customer       : " + reservation[1]);
            System.out.println("Amenities      : " + reservation[3]);
            System.out.printf("Total Amount   : PHP %.2f%n", total);
            System.out.printf("Amount Paid    : PHP %.2f%n", 0.0);
            System.out.printf("Discount       : PHP %.2f%n", 0.0);
            System.out.printf("Balance        : PHP %.2f%n", total);
            System.out.println("Payment Status : UNPAID");
            System.out.println("--------------------------------------");

            System.out.print("Print receipt now? (Y/N): ");
            if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                printReceiptById(id);
            }
        } else {
            System.out.println("Failed to save the bill.");
        }
    }



    private double calculateAmenityTotal(String reservationAmenities) {
        if (reservationAmenities == null
                || reservationAmenities.trim().isEmpty()) {
            return -1;
        }

        String[] selectedAmenities =
                reservationAmenities.split("\\s*\\+\\s*");

        ArrayList<String> amenities = data.readLines(amenityFile);
        double total = 0;

        for (String selected : selectedAmenities) {
            boolean found = false;

            for (String line : amenities) {
                String[] a = line.split(",", -1);

                if (a.length < 2) {
                    continue;
                }

                if (a[0].trim().equalsIgnoreCase(selected.trim())) {
                    try {
                        double price = Double.parseDouble(a[1].trim());

                        if (price < 0) {
                            System.out.println(
                                    "Invalid amenity price: " + selected);
                            return -1;
                        }

                        total += price;
                        found = true;
                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid amenity price: " + selected);
                        return -1;
                    }

                    break;
                }
            }

            if (!found) {
                System.out.println(
                        "Amenity not found in amenities file: " + selected);
                return -1;
            }
        }

        return total;
    }

   
    private void recordPayment(Scanner sc) {
        System.out.println("\n========== RECORD PAYMENT ==========");
        System.out.print("Enter Reservation ID: ");

        String id = sc.nextLine().trim();

        ArrayList<String> lines = data.readLines(transactionFile);
        int index = findTransactionIndex(lines, id);

        if (index < 0) {
            System.out.println("Bill not found. Create a bill first.");
            return;
        }

        String[] t = lines.get(index).split(",", -1);

        if (t.length < 5) {
            System.out.println("Invalid billing record.");
            return;
        }

        double total;
        double paid;

        try {
            total = Double.parseDouble(t[2].trim());
            paid = Double.parseDouble(t[3].trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid billing amounts.");
            return;
        }

        double balance = Math.max(0, total - paid);

        System.out.println("--------------------------------------");
        System.out.println("Reservation ID : " + t[0]);
        System.out.println("Customer       : " + t[1]);
        System.out.printf("Total Amount   : PHP %.2f%n", total);
        System.out.printf("Amount Paid    : PHP %.2f%n", paid);
        System.out.printf("Balance        : PHP %.2f%n", balance);
        System.out.println("Payment Status : " + t[4]);
        System.out.println("--------------------------------------");

        if (balance <= 0.005) {
            t[3] = String.valueOf(total);
            t[4] = "PAID";
            lines.set(index, join(t));

            if (data.writeLines(transactionFile, lines)) {
                reservations.markReservationPaid(id);
                System.out.println("This bill is already fully paid.");
                System.out.println("Reservation confirmed.");
            } else {
                System.out.println("Failed to update the billing record.");
            }
            return;
        }

        System.out.print("Enter payment amount: PHP ");

        double payment;

        try {
            payment = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid payment amount.");
            return;
        }

        if (payment <= 0) {
            System.out.println("Payment must be greater than zero.");
            return;
        }

        if (payment > balance + 0.005) {
            System.out.println("Payment exceeds the remaining balance.");
            return;
        }

        paid += payment;

        if (total - paid <= 0.005) {
            paid = total;
            t[4] = "PAID";
        } else {
            t[4] = "PARTIAL";
        }

        t[3] = String.valueOf(paid);
        lines.set(index, join(t));

        if (!data.writeLines(transactionFile, lines)) {
            System.out.println("Failed to save payment.");
            return;
        }

        System.out.println("\nPayment recorded successfully!");
        System.out.printf("Payment Received: PHP %.2f%n", payment);
        System.out.printf("Total Paid: PHP %.2f%n", paid);
        System.out.printf(
                "Remaining Balance: PHP %.2f%n",
                Math.max(0, total - paid));
        System.out.println("Payment Status: " + t[4]);

        if (t[4].equalsIgnoreCase("PAID")) {
            reservations.markReservationPaid(id);
            System.out.println("Reservation is now CONFIRMED.");
        }

        System.out.print("\nPrint receipt now? (Y/N): ");
        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
            printReceiptById(id);
        }
    }



    private void printReceipt(Scanner sc) {
        System.out.println("\n========== PRINT RECEIPT ==========");
        System.out.print("Enter Reservation ID: ");

        String id = sc.nextLine().trim();
        printReceiptById(id);
    }

    private void printReceiptById(String id) {
        ArrayList<String> transactions = data.readLines(transactionFile);
        int index = findTransactionIndex(transactions, id);

        if (index < 0) {
            System.out.println("Billing record not found.");
            return;
        }

   
        String[] t = transactions.get(index).split(",", -1);

        if (t.length < 5) {
            System.out.println("Invalid billing record.");
            return;
        }

        double total;
        double paid;

        try {
            total = Double.parseDouble(t[2].trim());
            paid = Double.parseDouble(t[3].trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid billing amounts.");
            return;
        }

        System.out.println();
        System.out.println("==============================================");
        System.out.println("                  RESTSORT");
        System.out.println("              PAYMENT RECEIPT");
        System.out.println("==============================================");
        System.out.println("Date           : " + LocalDate.now());
        System.out.println("Time           : "
                + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("hh:mm:ss a")));
        System.out.println("Reservation ID : " + t[0]);
        System.out.println("Customer       : " + t[1]);
        System.out.println("----------------------------------------------");
        System.out.printf("Total Bill     : PHP %.2f%n", total);
        System.out.printf("Amount Paid    : PHP %.2f%n", paid);
        System.out.printf(
                "Balance        : PHP %.2f%n",
                Math.max(0, total - paid));
        System.out.println("Payment Status : " + t[4]);
        System.out.println("==============================================");
        System.out.println("     Thank you for choosing RestSort!");
        System.out.println("==============================================");
        System.out.println("Receipt displayed successfully.");
    }


    private void showTransactions() {
        System.out.println("\n========== BILLING RECORDS ==========");

        ArrayList<String> transactions = data.readLines(transactionFile);

        if (transactions.isEmpty()) {
            System.out.println("No billing records found.");
            return;
        }

        for (String line : transactions) {
            String[] t = line.split(",", -1);

            if (t.length < 5) {
                continue;
            }

            try {
                double total = Double.parseDouble(t[2].trim());
                double paid = Double.parseDouble(t[3].trim());

                System.out.println("--------------------------------------");
                System.out.println("Reservation ID : " + t[0]);
                System.out.println("Customer       : " + t[1]);
                System.out.printf("Total Amount   : PHP %.2f%n", total);
                System.out.printf("Amount Paid    : PHP %.2f%n", paid);
                System.out.printf(
                        "Balance        : PHP %.2f%n",
                        Math.max(0, total - paid));
                System.out.println("Payment Status : " + t[4]);

                String[] stay = findStayRecord(t[0]);

                if (stay != null) {
                    System.out.println("Time In        : "
                            + (stay[3].isEmpty() ? "NOT YET" : stay[3]));
                    System.out.println("Time Out       : "
                            + (stay[4].isEmpty() ? "NOT YET" : stay[4]));
                    System.out.println("Stay Status    : " + stay[5]);
                } else {
                    System.out.println("Time In        : NOT YET");
                    System.out.println("Time Out       : NOT YET");
                    System.out.println("Stay Status    : NOT CHECKED IN");
                }
            } catch (NumberFormatException e) {
                System.out.println(
                        "Skipping invalid billing record: " + t[0]);
            }
        }

        System.out.println("--------------------------------------");
    }

 

    private void checkBalance(Scanner sc) {
        System.out.println("\n========== CHECK BALANCE ==========");
        System.out.print("Enter Reservation ID: ");

        String id = sc.nextLine().trim();

        ArrayList<String> transactions = data.readLines(transactionFile);
        int index = findTransactionIndex(transactions, id);

        if (index < 0) {
            System.out.println("Billing record not found.");
            return;
        }

    
        String[] t = transactions.get(index).split(",", -1);

        if (t.length < 5) {
            System.out.println("Invalid billing record.");
            return;
        }

        try {
            double total = Double.parseDouble(t[2].trim());
            double paid = Double.parseDouble(t[3].trim());

            System.out.println("--------------------------------------");
            System.out.println("Reservation ID : " + t[0]);
            System.out.println("Customer       : " + t[1]);
            System.out.printf("Total Amount   : PHP %.2f%n", total);
            System.out.printf("Amount Paid    : PHP %.2f%n", paid);
            System.out.printf(
                    "Balance        : PHP %.2f%n",
                    Math.max(0, total - paid));
            System.out.println("Payment Status : " + t[4]);

            String[] stay = findStayRecord(id);

            if (stay != null) {
                System.out.println("Time In        : "
                        + (stay[3].isEmpty() ? "NOT YET" : stay[3]));
                System.out.println("Time Out       : "
                        + (stay[4].isEmpty() ? "NOT YET" : stay[4]));
                System.out.println("Stay Status    : " + stay[5]);
            }

            System.out.println("--------------------------------------");
        } catch (NumberFormatException e) {
            System.out.println("Invalid billing record.");
        }
    }

    private void timeIn(Scanner sc) {
        System.out.println("\n========== CUSTOMER TIME IN ==========");

        reservations.showReservations();

        System.out.print("\nEnter Reservation ID: ");
        String id = sc.nextLine().trim();

        String[] reservation =
                reservations.getActiveReservationById(id);

        if (reservation == null || reservation.length < 7) {
            System.out.println("Reservation not found.");
            return;
        }

        if (!reservation[6].trim().equalsIgnoreCase("CONFIRMED")) {
            System.out.println(
                    "Check-in denied. Reservation must be fully paid.");
            return;
        }

        String reservationDate = reservation[2].trim();

        if (!reservationDate.equals(LocalDate.now().toString())) {
            System.out.println(
                    "Check-in is allowed only on the reservation date.");
            System.out.println("Reservation Date: " + reservationDate);
            System.out.println("Today's Date    : " + LocalDate.now());
            return;
        }

        ArrayList<String> stays = data.readLines(stayFile);

        for (String line : stays) {
            String[] s = line.split(",", -1);

            if (s.length >= 6
                    && s[0].trim().equalsIgnoreCase(id)) {

                if (s[5].trim().equalsIgnoreCase("CHECKED IN")) {
                    System.out.println("Customer is already checked in.");
                    System.out.println("Time In: " + s[3]);
                    return;
                }

                if (s[5].trim().equalsIgnoreCase("COMPLETED")) {
                    System.out.println(
                            "This reservation's stay is already completed.");
                    return;
                }
            }
        }

        String customer = clean(reservation[1]);
        String currentTime = LocalDateTime.now().format(dateTimeFormat);

        String stayRecord = clean(id) + ","
                + customer + ","
                + reservationDate + ","
                + currentTime + ",,CHECKED IN";

        if (data.appendLine(stayFile, stayRecord)) {
            System.out.println("\nCustomer checked in successfully!");
            System.out.println("Reservation ID: " + id);
            System.out.println("Customer     : " + customer);
            System.out.println("Time In      : " + currentTime);
        } else {
            System.out.println("Failed to save Time In record.");
        }
    }

    private void timeOut(Scanner sc) {
        System.out.println("\n========== CUSTOMER TIME OUT ==========");
        System.out.print("Enter Reservation ID: ");

        String id = sc.nextLine().trim();

        ArrayList<String> stays = data.readLines(stayFile);
        ArrayList<String> updated = new ArrayList<>();

        boolean found = false;

        for (String line : stays) {
            String[] s = line.split(",", -1);

            if (s.length >= 6
                    && s[0].trim().equalsIgnoreCase(id)
                    && s[5].trim().equalsIgnoreCase("CHECKED IN")) {

                found = true;
                s[4] = LocalDateTime.now().format(dateTimeFormat);
                s[5] = "COMPLETED";

                updated.add(join(s));

                System.out.println("\nCustomer checked out successfully!");
                System.out.println("Reservation ID: " + s[0]);
                System.out.println("Customer     : " + s[1]);
                System.out.println("Time In      : " + s[3]);
                System.out.println("Time Out     : " + s[4]);
                System.out.println("Stay Status  : " + s[5]);
            } else {
                updated.add(line);
            }
        }

        if (!found) {
            System.out.println(
                    "No active check-in found for that Reservation ID.");
            return;
        }

        if (data.writeLines(stayFile, updated)) {
            System.out.println("Stay record saved successfully.");
        } else {
            System.out.println("Failed to save Time Out record.");
        }
    }

    private void showStayRecords() {
        System.out.println("\n========== CUSTOMER STAY RECORDS ==========");

        ArrayList<String> stays = data.readLines(stayFile);

        if (stays.isEmpty()) {
            System.out.println("No stay records found.");
            return;
        }

        for (String line : stays) {
            String[] s = line.split(",", -1);

            if (s.length < 6) {
                continue;
            }

            System.out.println("--------------------------------------");
            System.out.println("Reservation ID : " + s[0]);
            System.out.println("Customer       : " + s[1]);
            System.out.println("Date           : " + s[2]);
            System.out.println("Time In        : "
                    + (s[3].isEmpty() ? "NOT YET" : s[3]));
            System.out.println("Time Out       : "
                    + (s[4].isEmpty() ? "NOT YET" : s[4]));
            System.out.println("Stay Status    : " + s[5]);
        }

        System.out.println("--------------------------------------");
    }

    private String[] findStayRecord(String id) {
        ArrayList<String> stays = data.readLines(stayFile);

        for (String line : stays) {
            String[] s = line.split(",", -1);

            if (s.length >= 6
                    && s[0].trim().equalsIgnoreCase(id)) {
                return s;
            }
        }

        return null;
    }


    private int findTransactionIndex(
            ArrayList<String> transactions, String id) {

        for (int i = 0; i < transactions.size(); i++) {
            String[] t = transactions.get(i).split(",", -1);

            if (t.length >= 5
                    && t[0].trim().equalsIgnoreCase(id)) {
                return i;
            }
        }

        return -1;
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

    private String join(String[] values) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                result.append(",");
            }

            result.append(values[i]);
        }

        return result.toString();
    }
}