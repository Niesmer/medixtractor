package AHA.medixtractor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Presentation {

  private String cip;
  private Integer cis;
  private Double prix;
  private String remboursement;
}
