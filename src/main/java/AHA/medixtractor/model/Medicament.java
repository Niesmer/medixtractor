package AHA.medixtractor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicament {

  private Integer cis;
  private String nom;
  private String forme;
  private String voie;
  private String laboratoire;
}
