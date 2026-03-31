package AHA.medixtractor.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Presentation;

@Repository
public interface PresentationRepository extends ListCrudRepository<Presentation, Long> {
    
}
