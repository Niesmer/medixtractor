package AHA.medixtractor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Medicament;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

}
