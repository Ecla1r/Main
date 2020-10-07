import java.io.*;
import java.sql.*;

public class Main {

    // connecting to RDS with JDBC
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://ecla1rdb.cxj3nzgx4xex.us-east-1.rds.amazonaws.com/", "postgres", "Ecla1r4111");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    // method that transferring DNA from .txt file to String
    public static String genomeReader (File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }
        br.close();
        return sb.toString();
    }

    public static void genomeSimilarityPercentage(int chainLength, String g1, String g2) throws SQLException {

        Statement statement = connect().createStatement();
        System.out.println("Finding Genome Similarity with " + chainLength + "-chains:");
        StringBuffer query = new StringBuffer();

        // trying to crate table, if table already exists, clearing it with TRUNCATE
        try {
            statement.executeUpdate("CREATE TABLE gen1g" + chainLength + "(gen varchar);\n");
        } catch (SQLException e) {
            statement.executeUpdate("TRUNCATE TABLE gen1g" + chainLength);
        }
        query.append("INSERT INTO gen1g" + chainLength + " (gen) VALUES ");
        for (int i = 0; i < g1.length() - chainLength + 1; i++) {
            String curGenome = "";
            for (int j = i; j < chainLength + i; j++) {
                curGenome += g1.charAt(j);
            }
            query.append("('" + curGenome + "')");
            if (i == g1.length() - chainLength) {
                query.append(";\n");
            } else query.append(", ");
        }
        System.out.println("First Genome added to base!");


        try {
            statement.executeUpdate("CREATE TABLE gen2g" + chainLength + "(gen varchar);\n");
        } catch (SQLException e) {
            statement.executeUpdate("TRUNCATE TABLE gen2g" + chainLength);
        }
        query.append("INSERT INTO gen2g" + chainLength + " (gen) VALUES ");
        for (int i = 0; i < g2.length() - chainLength + 1; i++) {
            String curGenome = "";
            for (int j = i; j < chainLength + i; j++) {
                curGenome += g2.charAt(j);
            }
            query.append("('" + curGenome + "')");
            if (i == g2.length() - chainLength) {
                query.append(";\n");
            } else query.append(", ");
        }
        System.out.println("Second Genome added to base!");


        statement.executeUpdate(query.toString());


        String union = "SELECT count(*) FROM (SELECT * FROM gen1g"+ chainLength + " UNION ALL SELECT * FROM gen2g" + chainLength + ") as cun";
        ResultSet rs1 = statement.executeQuery(union);
        rs1.next();
        double count = rs1.getDouble(1);

        String intrsct = "SELECT count(*) FROM (SELECT * FROM gen1g"+ chainLength + " INTERSECT ALL SELECT * FROM gen2g" + chainLength + ") as cis";
        ResultSet rs2 = statement.executeQuery(intrsct);
        rs2.next();
        double intersect = rs2.getDouble(1);

        System.out.println("Total Genomes: " + count);
        System.out.println("Intersected Genomes: " + intersect);
        System.out.println("Similarity: " + intersect / (count - intersect) + "\n");
    }

    public static void main (String [] args) throws IOException {

        File f1 = new File("Genome_1-1.txt");
        File f2 = new File("Genome_2-1.txt");

        String g1 = genomeReader(f1);
        String g2 = genomeReader(f2);

        try {
            genomeSimilarityPercentage(3, g1, g2);
            genomeSimilarityPercentage(5, g1, g2);
            genomeSimilarityPercentage(9, g1, g2);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

    }
}
