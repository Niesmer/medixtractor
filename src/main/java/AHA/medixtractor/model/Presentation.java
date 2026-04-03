package AHA.medixtractor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "presentations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Presentation {

  @Id
  private String cip;
  private Long cis;
  private Double prix;
  private String remboursement;
}
