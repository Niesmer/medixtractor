package com.medicaments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

@Entity
public class Composition {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer cis;
    private String substance;
    private Double dosage;
    private String unite;

    public Integer getId() { return id; }

    public Integer getCis() { return cis; }
    public void setCis(Integer cis) { this.cis = cis; }

    public String getSubstance() { return substance; }
    public void setSubstance(String substance) { this.substance = substance; }

    public Double getDosage() { return dosage; }
    public void setDosage(Double dosage) { this.dosage = dosage; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }
}