package jeetrackingproject.beans;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jeetrackingproject.utils.PasswordHasher;
import jeetrackingproject.utils.UtilsConnexion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@ManagedBean
@RequestScoped
public class RegisterBean {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private int age;
    private Double poids;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public void register() throws IOException {

        try {
            Connection con = UtilsConnexion.seConnecter();
            PasswordHasher passwordHasher = new PasswordHasher();
            String hashedPassword = passwordHasher.hashPassword(password);
            // Requête d'insertion des données
            String sql = "INSERT INTO sportif (nom, prenom, email, password, age, poids) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setInt(5, age);
            preparedStatement.setDouble(6, poids);

            // Exécution de la requête
            preparedStatement.executeUpdate();

            // Fermeture des ressources
            preparedStatement.close();
            con.close();
            nom = "";
            prenom = "";
            email = "";
            password = "";
            age = 0;
            poids = 0.0;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Inscription réussie", "Veuillez vérifier votre nom d'utilisateur et votre mot de passe.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Une erreur est survenue"+e.getMessage(), "Veuillez réessayer.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

}
