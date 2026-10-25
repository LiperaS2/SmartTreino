package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexaoBanco {

    private ConexaoBanco() {
    }

    // Carrega o driver MySQL e abre uma conexão com o banco SmartTreino.
    public static Connection abrir() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3308/SmartTreino", "root", "Deusvaiguiar123");
    }
}
