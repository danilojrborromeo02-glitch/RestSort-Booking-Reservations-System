package RestSort;

import java.util.ArrayList;
import java.util.Scanner;

public class DiscountManagement {

    private final String fileName =
            "C:\\Users\\Ailyn\\eclipse-workspace\\RESTSORT\\src\\RestSort\\discount";

    private final DataManager data = new DataManager();
    private final Scanner sc;

    public DiscountManagement(Scanner sc) {
        this.sc = sc;
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value.replace(",", " ")
                    .replace("|", " ")
                    .replace("\n", " ")
                    .replace("\r", " ")
                    .trim();
    }

    public void menu() {

        while (true) {
            System.out.println("\n========================================");
            System.out.println("          DISCOUNT MANAGEMENT");
            System.out.println("========================================");
            System.out.println("[1] Add Discount");
            System.out.println("[2] View Discounts");
            System.out.println("[3] Edit Discount");
            System.out.println("[4] Delete Discount");
            System.out.println("[5] Back to Agent Dashboard");
            System.out.println("========================================");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    applyDiscount(sc);
                    break;
                case "2":
                    showDiscounts();
                    break;
                case "3":
                    editDiscount();
                    break;
                case "4":
                    deleteDiscount();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice. Choose 1-5.");
            }
        }
    }

 
    public void applyDiscount(Scanner sc) {

        System.out.println("\n========== ADD DISCOUNT ==========");

        System.out.print("Customer name: ");
        String customer = clean(sc.nextLine());

        if (customer.isEmpty()) {
            System.out.println("Customer name is required.");
            return;
        }

        System.out.print("Discount description: ");
        String description = clean(sc.nextLine());

        if (description.isEmpty()) {
            System.out.println("Discount description is required.");
            return;
        }

        double percent;

        try {
            System.out.print("Discount percentage (greater than 0 to 100): ");
            percent = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid percentage.");
            return;
        }

        if (!Double.isFinite(percent) || percent <= 0 || percent > 100) {
            System.out.println("Percentage must be greater than 0 and at most 100.");
            return;
        }

        String id = generateDiscountId();

    
        String record = id + ","
                + customer + ","
                + description + ","
                + percent;

        if (data.appendLine(fileName, record)) {
            System.out.println("\nDiscount saved successfully!");
            System.out.println("Discount ID: " + id);
            System.out.println("Customer: " + customer);
            System.out.println("Description: " + description);
            System.out.println("Discount: " + percent + "%");
        } else {
            System.out.println("Unable to save discount.");
        }
    }

  
    public void showDiscounts() {

        ArrayList<String> records = data.readLines(fileName);

        System.out.println("\n================ DISCOUNT RECORDS ================");

        if (records.isEmpty()) {
            System.out.println("No discounts found.");
            return;
        }

        for (String record : records) {
            String[] d = record.split(",", -1);

            if (d.length >= 4) {
                System.out.println("----------------------------------------");
                System.out.println("Discount ID : " + d[0]);
                System.out.println("Customer    : " + d[1]);
                System.out.println("Description : " + d[2]);
                System.out.println("Percentage  : " + d[3] + "%");
            }
        }

        System.out.println("----------------------------------------");
    }


    public void editDiscount() {

        System.out.println("\n========== EDIT DISCOUNT ==========");
        showDiscounts();

        System.out.print("\nEnter Discount ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("Discount ID is required.");
            return;
        }

        ArrayList<String> records = data.readLines(fileName);
        ArrayList<String> updated = new ArrayList<>();
        boolean found = false;

        for (String record : records) {
            String[] d = record.split(",", -1);

            if (d.length >= 4 && d[0].trim().equalsIgnoreCase(id)) {

                found = true;

                System.out.println("Press Enter to keep the current value.");

                System.out.print("Customer name [" + d[1] + "]: ");
                String customer = clean(sc.nextLine());

                if (customer.isEmpty()) {
                    customer = d[1].trim();
                }

                System.out.print("Description [" + d[2] + "]: ");
                String description = clean(sc.nextLine());

                if (description.isEmpty()) {
                    description = d[2].trim();
                }

                System.out.print("Percentage [" + d[3] + "]: ");
                String input = sc.nextLine().trim();

                double percent;

                try {
                    percent = input.isEmpty()
                            ? Double.parseDouble(d[3].trim())
                            : Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid percentage. Edit cancelled.");
                    return;
                }

                if (!Double.isFinite(percent) || percent <= 0 || percent > 100) {
                    System.out.println("Percentage must be greater than 0 and at most 100.");
                    return;
                }

                updated.add(d[0].trim() + ","
                        + customer + ","
                        + description + ","
                        + percent);

            } else {
                updated.add(record);
            }
        }

        if (!found) {
            System.out.println("Discount ID not found.");
            return;
        }

        if (data.writeLines(fileName, updated)) {
            System.out.println("Discount updated successfully.");
        } else {
            System.out.println("Unable to update discount file.");
        }
    }

   
    public void deleteDiscount() {

        System.out.println("\n========== DELETE DISCOUNT ==========");
        showDiscounts();

        System.out.print("\nEnter Discount ID to delete: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("Discount ID is required.");
            return;
        }

        System.out.print("Are you sure you want to delete it? (Y/N): ");

        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        ArrayList<String> records = data.readLines(fileName);
        ArrayList<String> updated = new ArrayList<>();
        boolean found = false;

        for (String record : records) {
            String[] d = record.split(",", -1);

            if (d.length >= 4 && d[0].trim().equalsIgnoreCase(id)) {
                found = true;
            } else {
                updated.add(record);
            }
        }

        if (!found) {
            System.out.println("Discount ID not found.");
            return;
        }

        if (data.writeLines(fileName, updated)) {
            System.out.println("Discount deleted successfully.");
        } else {
            System.out.println("Unable to delete discount.");
        }
    }


    public ArrayList<String[]> getDiscountsForCustomer(String customer) {

        ArrayList<String[]> matching = new ArrayList<>();

        for (String record : data.readLines(fileName)) {
            String[] d = record.split(",", -1);

            if (d.length >= 4
                    && d[1].trim().equalsIgnoreCase(customer.trim())) {
                try {
                    double percent = Double.parseDouble(d[3].trim());

                    if (Double.isFinite(percent) && percent > 0 && percent <= 100) {
                        matching.add(d);
                    }
                } catch (NumberFormatException ignored) {
              
                }
            }
        }

        return matching;
    }

    private String generateDiscountId() {

        int highestId = 0;

        for (String record : data.readLines(fileName)) {
            String[] d = record.split(",", -1);

            if (d.length > 0 && d[0].trim().startsWith("DC")) {
                try {
                    int number = Integer.parseInt(d[0].trim().substring(2));
                    highestId = Math.max(highestId, number);
                } catch (NumberFormatException ignored) {
                                  }
            }
        }

        return "DC" + (highestId + 1);
    }
}