package RestSort;

import java.util.Scanner;

public class MAIN {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AccountManager accounts = new AccountManager();

        while (true) {
            System.out.println("\n====================================");
            System.out.println("          RESTSORT SYSTEM");
            System.out.println("====================================");
            System.out.println("[1] Admin Login");
            System.out.println("[2] Agent Login");
            System.out.println("[3] Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Username: ");
                String username = sc.nextLine().trim();

                System.out.print("Password: ");
                String password = sc.nextLine();

                ACCOUNT account = accounts.login(
                        username, password, "admin");

                if (account != null) {
                    System.out.println("Welcome, " + account.getName() + "!");
                    new ADMIN(sc, accounts).menu();
                } else {
                    System.out.println("Invalid username or password.");
                }

            } else if (choice.equals("2")) {
                System.out.print("Username: ");
                String username = sc.nextLine().trim();

                System.out.print("Password: ");
                String password = sc.nextLine();

                ACCOUNT account = accounts.login(
                        username, password, "agent");

                if (account != null) {
                    System.out.println("Welcome, " + account.getName() + "!");
                    new AGENT(sc, account.getName()).menu();
                } else {
                    System.out.println("Invalid username or password.");
                }

            } else if (choice.equals("3")) {
                System.out.println("Thank you for using RESTSORT!");
                sc.close();
                break;

            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}