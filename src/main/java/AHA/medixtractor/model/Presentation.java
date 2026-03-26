package com.medicaments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Presentation {

    @Id
    private String cip;
    private Integer cis;
    private Double prix;
    private String remboursement;

    public String getCip() { return cip; }
    public void setCip(String cip) { this.cip = cip; }

    public Integer getCis() { return cis; }
    public void setCis(Integer cis) { this.cis = cis; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public String getRemboursement() { return remboursement; }
    public void setRemboursement(String remboursement) { this.remboursement = remboursement; }
}