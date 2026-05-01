package AHA.medixtractor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medicament")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicament {

    @Id
    private Long cis;

    @Column(nullable = false)
    private String nom;

    private String forme;
    private String voie;
    private String statut;
    private String procedure;
    private String commercialisation;

    @Column(name = "date_amm")
    private String dateAmm;

    private String laboratoire;
}
