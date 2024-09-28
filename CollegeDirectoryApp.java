import java.util.*;
import java.sql.*;

public class CollegeDirectoryApp {
    // Connection details
    static final String DB_URL = "jdbc:postgresql://localhost:5432/college_directory";
    static final String USER = "postgres";
    static final String PASS = "yourpassword";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to College Directory");
            System.out.println("Select Role: 1. Student  2. Faculty  3. Admin  4. Exit");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    studentLogin();
                    break;
                case 2:
                    facultyLogin();
                    break;
                case 3:
                    adminLogin();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Example login function for all roles
    public static void studentLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Username: ");
        String username = sc.next();
        System.out.print("Enter Password: ");
        String password = sc.next();

        if (validateUser(username, password, "STUDENT")) {
            System.out.println("Welcome, " + username);
            studentDashboard(username);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    public static void facultyLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Username: ");
        String username = sc.next();
        System.out.print("Enter Password: ");
        String password = sc.next();

        if (validateUser(username, password, "FACULTY")) {
            System.out.println("Welcome, " + username);
            facultyDashboard(username);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    public static void adminLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Username: ");
        String username = sc.next();
        System.out.print("Enter Password: ");
        String password = sc.next();

        if (validateUser(username, password, "ADMIN")) {
            System.out.println("Welcome, " + username);
            adminDashboard(username);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    // Validate user credentials
    public static boolean validateUser(String username, String password, String role) {
        boolean isValid = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isValid = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    // Student Dashboard: View profile and enrollments
    public static void studentDashboard(String username) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Student Dashboard: ");
            System.out.println("1. View Profile  2. View Courses  3. Logout");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewStudentProfile(username);
                    break;
                case 2:
                    viewEnrolledCourses(username);
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Faculty Dashboard: View class list
    public static void facultyDashboard(String username) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Faculty Dashboard: ");
            System.out.println("1. View Class List  2. Logout");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewFacultyClassList(username);
                    break;
                case 2:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Admin Dashboard: Manage users and view stats
    public static void adminDashboard(String username) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Admin Dashboard: ");
            System.out.println("1. Manage Students/Faculty  2. View Stats  3. Logout");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    manageUsers();
                    break;
                case 2:
                    viewStats();
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Function to view student profile
    public static void viewStudentProfile(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM users JOIN student_profile ON users.id = student_profile.user_id WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Year: " + rs.getString("year"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function to view enrolled courses
    public static void viewEnrolledCourses(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT c.title FROM enrollment e JOIN course c ON e.course_id = c.id JOIN users u ON e.student_id = u.id WHERE u.username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            System.out.println("Enrolled Courses:");
            while (rs.next()) {
                System.out.println(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function to view faculty class list
    public static void viewFacultyClassList(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT s.name FROM enrollment e JOIN student_profile s ON e.student_id = s.user_id JOIN users u ON e.course_id IN (SELECT id FROM course WHERE faculty_id = u.id) WHERE u.username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            System.out.println("Class List:");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Admin functionalities: Manage users
    public static void manageUsers() {
        System.out.println("Managing users... (Add, Delete, Update)");
        // Implement CRUD operations for students, faculty, etc.
    }

    // View stats like enrollment trends
    public static void viewStats() {
        System.out.println("Viewing stats... (e.g., Enrollment trends)");
        // Implement data statistics and reporting.
    }
}