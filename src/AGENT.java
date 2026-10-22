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