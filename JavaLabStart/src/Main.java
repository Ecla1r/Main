import models.Student;
import repositories.StudentsRepository;
import repositories.StudentsRepositoryJdbcImpl;

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

        Student s = new Student(30L, "Grigory", "Khaykin", 19, 901);
        StudentsRepository studentsRepository = new StudentsRepositoryJdbcImpl(connection);
        //System.out.println(studentsRepository.findById(2L));
        //studentsRepository.save(s);
        System.out.println(studentsRepository.findAll());
        System.out.println(studentsRepository.findAllByAge(26));
        System.out.println(studentsRepository.findById(2L));
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
