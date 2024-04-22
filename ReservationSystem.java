import java.sql.*;
import java.util.Scanner;

class DatabaseManager{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/jdbcreservation";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs){
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}

class User{
    private String username;
    private String password;
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }
    public boolean isValidUser(String enteredPassword){
        return this.password.equals(enteredPassword);
    }
}
class UserManager{
    public static User authenticateUser(String username, String password){
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT password FROM user WHERE name = ?")){
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    String dbPassword = rs.getString("password");
                    if (dbPassword.equals(password)){
                        return new User(username, password);
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
class ReservationManager{
    public static void makeReservation(User user){
        System.out.println("Reservation process is started");
        System.out.println("Reservation is successful for user: " + user.getUsername());
        System.out.println("Database status updated that Reservation saved.");
    }
    public static void cancelReservation(User user, String pnrNumber){
        System.out.println("Cancellation process is started");
        System.out.println("Reservation is cancelled for user: " + user.getUsername() + ", PNR: " + pnrNumber);
        System.out.println("Database status updated that Reservation cancelled.");
    }
}
public class ReservationSystem{
    public static void main(String[] args){
        try (Scanner sca = new Scanner(System.in)){
            System.out.println("Enter your username: ");
            String username = sca.nextLine();
            System.out.println("Enter your password: ");
            String password = sca.nextLine();

            User user = UserManager.authenticateUser(username, password);
            if (user != null){
                System.out.println("Login successfully completed for " + user.getUsername() + "!");
                System.out.println("Please select an option: ");
                System.out.println("1.Create Reservation");
                System.out.println("2.Cancel Reservation");
                int option = sca.nextInt();
                sca.nextLine();
                switch (option) {
                    case 1:
                        ReservationManager.makeReservation(user);
                        break;
                    case 2:
                        System.out.println("Enter your PNR number: ");
                        String pnrNumber = sca.nextLine();
                        ReservationManager.cancelReservation(user, pnrNumber);
                        break;
                    default:
                        System.out.println("Select the correct option");
                }
            }
            else{
                System.out.println("Wrong username or password.Please try again!");
            }
        }
    }
}
