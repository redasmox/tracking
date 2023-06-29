package jeetrackingproject.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import jeetrackingproject.entities.Planning;
import jeetrackingproject.entities.Point;
import jeetrackingproject.utils.UtilsConnexion;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class PointBean implements Serializable {

    Planning planning;
    private List<Point> points;
    private Point selectedPoint;

    public Planning getPlanning() {
        return planning;
    }

    public void setPlanning(Planning planning) {
        this.planning = planning;
    }

    private MapModel<Long> simpleModel;

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Point getSelectedPoint() {
        return selectedPoint;
    }

    public void setSelectedPoint(Point selectedPoint) {
        this.selectedPoint = selectedPoint;
    }

    public MapModel<Long> getSimpleModel() {
        return simpleModel;
    }

    public void setSimpleModel(MapModel<Long> simpleModel) {
        this.simpleModel = simpleModel;
    }

    public void onMarkerSelect(OverlaySelectEvent event) {
        Marker marker = (Marker) event.getOverlay();
        // Récupérer le point de localisation sélectionné à partir du marker
        selectedPoint = (Point) marker.getData();
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        Long planning_id = Long.parseLong(request.getParameter("planning_id"));
        this.points = getAllPoints(planning_id);
        this.planning = getPlanning(planning_id);
    }

    public List<Point> getAllPoints(Long planning_id) {
        points = new ArrayList<>();
        simpleModel = new DefaultMapModel<>();
        Long cpt = 1L;
        ResultSet rs = null;
        try {
            Connection con = UtilsConnexion.seConnecter();
            String sql = "SELECT * FROM point WHERE planning_id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
//            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setLong(1, planning_id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                Double latitude = Double.parseDouble(rs.getString("latitude"));
                Double longitude = Double.parseDouble(rs.getString("longitude"));
                String heure = rs.getString("heure");
                Point point = new Point();
                point.setId(id);
                point.setHour(heure);
                point.setLatitude(latitude);
                point.setLongitude(longitude);
                simpleModel.addOverlay(new Marker<>(new LatLng(latitude, longitude), heure, cpt));
                cpt++;
                points.add(point);
            }
            preparedStatement.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return points;
    }

    public Planning getPlanning(Long planning_id) {
        this.planning = new Planning();
        ResultSet rs = null;
        try {
            Connection con = UtilsConnexion.seConnecter();
            String sql = "SELECT * FROM planning WHERE id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setLong(1, planning_id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                String heured = rs.getString("heured");
                String heuref = rs.getString("heuref");
                Long duree = Long.parseLong(rs.getString("duree"));
                Long distance = Long.parseLong(rs.getString("distance"));
                this.planning.setDate(date);
                this.planning.setHeured(heured);
                this.planning.setHeuref(heuref);
                planning.setDistance(distance);
                planning.setDuree(duree);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("++++Date++>+" + planning.getDate());

        return this.planning;
    }

}
