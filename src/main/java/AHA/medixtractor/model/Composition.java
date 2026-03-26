package com.medicaments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Composition {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer cis;
    private String substance;
    private Double dosage;
    private String unite;
}