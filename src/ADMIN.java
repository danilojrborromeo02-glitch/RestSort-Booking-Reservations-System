package RestSort;

import java.util.Scanner;

public class ADMIN {

    private final Scanner sc;
    private final AccountManager accounts;
    private final ReservationManagement reservations;

    public ADMIN(Scanner sc, AccountManager accounts) {
        this.sc = sc;
        this.accounts = accounts;
        this.reservations = new ReservationManagement();
    }

    public void menu() {
        while (true) {
            System.out.println("\n========== ADMIN DASHBOARD ==========");
            System.out.println("[1] Add Agent Account");
            System.out.println("[2] View Accounts");
            System.out.println("[3] Delete Agent Account");
            System.out.println("[4] View Reservations");
            System.out.println("[5] Cancel Reservation");
            System.out.println("[6] Logout");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addAgent();
                    break;

                case "2":
                    accounts.showAccounts();
                    break;

                case "3":
                    System.out.print("Agent username to delete: ");
                    accounts.deleteAccount(sc.nextLine().trim());
                    break;

                case "4":
                    reservations.showReservations();
                    break;

                case "5":
                    System.out.print("Reservation ID to cancel: ");
                    reservations.cancelReservation(sc.nextLine().trim());
                    break;

                case "6":
                    System.out.println("Admin logged out.");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addAgent() {
        try {
            System.out.print("Agent name: ");
            String name = sc.nextLine().trim();

            System.out.print("Agent username: ");
            String username = sc.nextLine().trim();

            System.out.print("Agent password: ");
            String password = sc.nextLine();

            ACCOUNT agent = new ACCOUNT(
                    username, password, name, "agent");

            accounts.addAccount(agent);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }
}