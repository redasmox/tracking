package jeetrackingproject.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UtilsConnexion {
    public static Connection seConnecter() throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection("jdbc:postgresql://localhost/bdt", "postgres", "reda");
        }catch (SQLException e){
            throw new Exception("Impossible de se connecter à la base de donnée");
        }
    }
}
