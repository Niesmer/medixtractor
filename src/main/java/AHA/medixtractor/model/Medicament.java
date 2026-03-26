package com.medicaments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicament {

    @Id
    private Integer cis;
    private String nom;
    private String forme;
    private String voie;
    private String laboratoire;
}