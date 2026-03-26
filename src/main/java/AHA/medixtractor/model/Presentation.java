package com.medicaments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Presentation {

    @Id
    private String cip;
    private Integer cis;
    private Double prix;
    private String remboursement;
}