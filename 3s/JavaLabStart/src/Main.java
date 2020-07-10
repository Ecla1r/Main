import java.sql.*;

public class Main {

    private static final String URL = "jdbc:postgresql://localhost:5432/JavaLab";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Ecla1r4111";


    public static Connection openConnection (String url, String user, String password) {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void main (String [] args) {

        Connection connection = openConnection(URL, USER, PASSWORD);

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from years_of_ruling y join rulers r on y.ruler_id = r.id where last_year < 1800");
            System.out.println("These are the rulers who reigned only before XIXth century:");
            while (rs.next()) {
                System.out.println(rs.getInt("first_year") + " - " + rs.getInt("last_year") + ", " + rs.getString("name"));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

        try {
            connection.close();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
