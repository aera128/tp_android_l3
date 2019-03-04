package fr.unicaen.aera128.immobilier.Models;

import java.io.Serializable;

/**
 * Modèle d'un vendeur
 */
public class Vendeur implements Serializable {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    /**
     * Getter Setters
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Redéfinition du toString()
     */
    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
