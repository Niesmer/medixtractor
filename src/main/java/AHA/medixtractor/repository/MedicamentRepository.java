package AHA.medixtractor.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Medicament;

@Repository
public interface MedicamentRepository extends ListCrudRepository<Medicament, Long> {
    
}
