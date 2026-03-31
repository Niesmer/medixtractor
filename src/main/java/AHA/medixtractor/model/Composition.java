package AHA.medixtractor.model;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Composition {

  @Id
  private Integer id;

  private Integer cis;
  private String substance;
  private Double dosage;
  private String unite;
}
