package infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsável por abrir conexões com o banco Postgres.
 * Cada repositório pede uma conexão nova por operação (try-with-resources),
 * então não precisamos nos preocupar em fechar/reaproveitar conexão aqui.
 */
public class ConnectionFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/cantina";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possível conectar ao banco de dados: " + e.getMessage(), e);
        }
    }
}
