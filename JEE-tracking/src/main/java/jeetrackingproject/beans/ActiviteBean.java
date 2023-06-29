package jeetrackingproject.beans;

import jakarta.annotation.ManagedBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jeetrackingproject.entities.Activite;
import jeetrackingproject.utils.UtilsConnexion;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class ActiviteBean implements Serializable {
    private String nom;
    private String message;
    private List<Activite> activites;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

    @PostConstruct
    public void init() {
        this.activites = getActivitesByUsername();
    }

    public List<Activite> getActivitesByUsername() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        HttpSession session = request.getSession();

        if (session.getAttribute("id") != null) {
            int id1 = (int) session.getAttribute("id");
            List<Activite> activites = new ArrayList<>();
            ResultSet rs = null;
            try {
                Connection con = UtilsConnexion.seConnecter();
                String sql = "SELECT * FROM activite WHERE sportif_id = ?";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setLong(1, id1);
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String nom = rs.getString("nom");
                    Activite activite = new Activite();
                    activite.setId(id);
                    activite.setNom(nom);
                    activites.add(activite);
                }
                preparedStatement.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return activites;
        } else {
            try {
                externalContext.redirect("login.xhtml");
            } catch (Exception e) {

            }
        }
        return activites;
    }

    public void saveActivite() throws Exception {
        ResultSet rs = null;
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        HttpSession session = request.getSession();
        int id = (int) session.getAttribute("id");
        try {
            Connection con = UtilsConnexion.seConnecter();
            String sql = "INSERT INTO activite (nom, sportif_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            System.out.println("Dedans....2");
            System.out.println("ID dans save activite" + id);
            preparedStatement.setString(1, nom);
            preparedStatement.setLong(2, id);
            // Exécution de la requête
            preparedStatement.executeUpdate();

            // Fermeture des ressources
            preparedStatement.close();
            con.close();
            nom = "";
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Activité ajoué avec succès!!!", "Une erreur s'est produite lors de la connexion à la base de données.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            externalContext.redirect("home.xhtml");
        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur de base de données", "Une erreur s'est produite lors de la connexion à la base de données.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
