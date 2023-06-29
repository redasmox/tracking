package jeetrackingproject.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PointMethod {
    public static void savePosition(Long idPlanning) throws Exception {
        int cmpt = 0;
        while (cmpt < 2) {
            Connection con = UtilsConnexion.seConnecter();
            String sql = "INSERT INTO point (planning_id, latitude, longitude, heure) VALUES (?, ?, ?, ?)";
            Double longitude = Math.random() * (90 - (-90)) - 90;
            Double latitude = Math.random() * (180 - (-180)) - 180;
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String hour = currentTime.format(formatter);
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setLong(1, idPlanning);
            preparedStatement.setDouble(2, latitude);
            preparedStatement.setDouble(3, longitude);
            preparedStatement.setString(4, hour);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            con.close();
            cmpt++;
            Thread.sleep(100000);
        }
    }

    public String initPlanning() {

        return "";
    }
}
