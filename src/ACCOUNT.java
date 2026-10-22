package RestSort;

public class ACCOUNT {

    private String username;
    private String password;
    private String name;
    private String role;

    public ACCOUNT(String username, String password, String name, String role) {
        setUsername(username);
        setPassword(password);
        setName(name);
        setRole(role);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        this.username = username.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        this.password = password;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        this.name = name.trim();
    }

    public void setRole(String role) {
        if (role == null
                || (!role.equalsIgnoreCase("admin")
                && !role.equalsIgnoreCase("agent"))) {
            throw new IllegalArgumentException("Invalid role.");
        }

        this.role = role.trim().toLowerCase();
    }
}
AJ
package RestSort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class DataManager {

    public ArrayList<String> readLines(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        Path path = Paths.get(fileName);

        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(path)) {
                Files.createFile(path);
                return lines;
            }

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        lines.add(line);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return lines;
    }

    public boolean appendLine(String fileName, String content) {
        Path path = Paths.get(fileName);

        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {

                writer.write(content);
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            return false;
        }
    }

    public boolean writeLines(String fileName, ArrayList<String> lines) {
        Path path = Paths.get(fileName);

        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error updating file: " + e.getMessage());
            return false;
        }
    }
}