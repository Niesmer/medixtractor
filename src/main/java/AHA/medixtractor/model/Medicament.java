package com.medicaments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Medicament {

    @Id
    private Integer cis;
    private String nom;
    private String forme;
    private String voie;
    private String laboratoire;

    public Integer getCis() { return cis; }
    public void setCis(Integer cis) { this.cis = cis; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getForme() { return forme; }
    public void setForme(String forme) { this.forme = forme; }

    public String getVoie() { return voie; }
    public void setVoie(String voie) { this.voie = voie; }

    public String getLaboratoire() { return laboratoire; }
    public void setLaboratoire(String laboratoire) { this.laboratoire = laboratoire; }
}