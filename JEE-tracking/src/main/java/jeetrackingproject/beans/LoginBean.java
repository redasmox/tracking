package jeetrackingproject.beans;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jeetrackingproject.utils.PasswordHasher;
import jeetrackingproject.utils.UtilsConnexion;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ManagedBean
@RequestScoped
public class LoginBean {
    private String email;
    private String password;
    private String msg;
    private String nom;
    //HttpServletRequest request1 = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    HttpSession session;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void login() throws IOException {
        ResultSet rs = null;
        try {
            Connection con = UtilsConnexion.seConnecter();
            String sql = "SELECT * FROM sportif WHERE email = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, email);
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                PasswordHasher passwordHasher = new PasswordHasher();
                String password1 = rs.getString(6);
                boolean result = passwordHasher.verifyPassword(password, password1);
                if (result) {
                    int id = Integer.parseInt(rs.getString(3));
                    nom = rs.getString(5);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("utilisateur", email);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message succes", "Auth résussi." + nom);
                    msg = "Auth résussi." + id;
                    //request1.getSession().setAttribute("id", id);
                    session = request.getSession();
                    session.setAttribute("id", id);
                    session.setAttribute("nom", nom);
                    int valeur = (int) session.getAttribute("id");
                    preparedStatement.close();
                    con.close();
                    externalContext.redirect("home.xhtml");
                } else {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Identifiants invalides", "Veuillez vérifier votre mot de passe.");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }

            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "User inconnu", "Veuillez vérifier votre nom d'utilisateur.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur de base de données", "Une erreur s'est produite lors de la connexion à la base de données.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void logout() throws IOException {
        this.session.removeAttribute("id");
        this.session.removeAttribute("nom");
        externalContext.redirect("login.xhtml");
    }
}
