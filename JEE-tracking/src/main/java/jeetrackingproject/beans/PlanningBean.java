package jeetrackingproject.beans;

import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jeetrackingproject.entities.Planning;
import jeetrackingproject.utils.EnregistrementPositionGPS;
import jeetrackingproject.utils.PointMethod;
import jeetrackingproject.utils.UtilsConnexion;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ManagedBean
@SessionScoped
public class PlanningBean implements Serializable {

    public static LocalTime GLOBALCURRENTTIME;
    private Long id;
    @ManagedProperty(value = "#{param.activite_id}")
    private Long activite_id;
    private String date;
    private String heured;
    private String heuref;
    private List<Planning> plannings;
    public static ScheduledExecutorService scheduler;

    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    HttpSession session;

    public List<Planning> getPlannings() {
        return plannings;
    }

    public void setPlannings(List<Planning> plannings) {
        this.plannings = plannings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivite_id() {
        return activite_id;
    }

    public void setActivite_id(Long activite_id) {
        this.activite_id = activite_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeured() {
        return heured;
    }

    public void setHeured(String heured) {
        this.heured = heured;
    }

    public String getHeuref() {
        return heuref;
    }

    public void setHeuref(String heuref) {
        this.heuref = heuref;
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        String activite_idS = request.getParameter("activite_id");
        if (activite_idS != null) {
            activite_id = Long.parseLong(activite_idS);
            this.plannings = getAllPlannigByActivite(activite_id);
            session = request.getSession();
            session.setAttribute("activite_id", activite_id);
        } else {
            session = request.getSession();
            this.plannings = getAllPlannigByActivite((Long) session.getAttribute("activite_id"));
            session.removeAttribute("activite_id");
        }
    }

    public List<Planning> getAllPlannigByActivite(Long activite_id) {
        this.plannings = new ArrayList<>();
        ResultSet rs = null;
        try {
            Connection con = UtilsConnexion.seConnecter();
            String sql = "SELECT * FROM planning WHERE activite_id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setLong(1, activite_id);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Long id1 = rs.getLong("id");
                String date1 = rs.getString("date");
                String heured1 = rs.getString("heured");
                String heuref1 = rs.getString("heuref");
                Long distance = rs.getLong("distance");
                Long duree = rs.getLong("duree");
                Planning planning = new Planning();
                planning.setId(id1);
                planning.setDate(date1);
                planning.setHeured(heured1);
                planning.setHeuref(heuref1);
                planning.setDuree(duree);
                planning.setDistance(distance);
                plannings.add(planning);
            }
            preparedStatement.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plannings;
    }

    public void redirectToPlanning(Long activite_id) throws IOException {
        getAllPlannigByActivite(activite_id);
        externalContext.redirect("planning.xhtml");
    }

    public void initPlannig() {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            Long activite_id1 = Long.parseLong(request.getParameter("activite_id"));
            Connection con = UtilsConnexion.seConnecter();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String todayDate = dtf.format(now);
            LocalTime currentTime = LocalTime.now();
            GLOBALCURRENTTIME = currentTime;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String actuTime = currentTime.format(formatter);

            String sql = "INSERT INTO planning (activite_id, date, heured) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setLong(1, activite_id1);
            preparedStatement.setString(2, todayDate);
            preparedStatement.setString(3, actuTime);
            preparedStatement.executeUpdate();

            Long idPlanning = getIdPlanning(activite_id1, todayDate, actuTime);
            session = request.getSession();
            session.setAttribute("idPlanning", idPlanning);
            // Fermeture des ressources
            preparedStatement.close();
            con.close();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Activité lancé", "Activity is running.");
            FacesContext.getCurrentInstance().addMessage(null, message);
//            PointMethod.savePosition(idPlanning);
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new EnregistrementPositionGPS(idPlanning), 0, 1, TimeUnit.MINUTES);
            //scheduler.scheduleAtFixedRate(new EnregistrementPositionGPS(idPlanning), 0, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Long getIdPlanning(Long id_ativite, String datePlanning, String heurePlanning) throws Exception {
        Connection con = UtilsConnexion.seConnecter();
        String sql = "SELECT * FROM planning WHERE activite_id = ? AND date = ? AND heured = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement = con.prepareStatement(sql);
        preparedStatement.setLong(1, id_ativite);
        preparedStatement.setString(2, datePlanning);
        preparedStatement.setString(3, heurePlanning);
        ResultSet rs = preparedStatement.executeQuery();
        Long idPlanning = null;
        if (rs.next()) {
            idPlanning = Long.parseLong(rs.getString(4));
        }
        preparedStatement.close();
        con.close();
        return idPlanning;
    }

    public void stopPlanning() throws Exception {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String actuTime = currentTime.format(formatter);
        session = request.getSession();
        Long idPlanning = (Long) session.getAttribute("idPlanning");
        System.out.println("------------++++++++++++++++++++++idPlanning++++++++++++++++++++++-" + idPlanning);
        Connection con = UtilsConnexion.seConnecter();
        String sql = "UPDATE planning SET heuref = ? WHERE id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, actuTime);
        preparedStatement.setLong(2, idPlanning);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        con.close();
        session.removeAttribute("idPlanning");
    }
}
