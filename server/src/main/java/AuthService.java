import java.sql.*;


public class AuthService {
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String URL_DB = "jdbc:sqlite:server\\USERS.DB";

    public boolean checkPass(String login, String pass) throws ClassNotFoundException, SQLException {
        boolean result = false;
        Class.forName(JDBC_DRIVER);
        try (Connection connection = DriverManager.getConnection(URL_DB)) {
            ResultSet rs1 = connection.createStatement().executeQuery("SELECT * FROM Client WHERE LOGIN = '" + login + "' ;");
            if (!rs1.isClosed()) {
                result = pass.equals(rs1.getString("PASS"));
            }
        }
        return result;
    }
}
