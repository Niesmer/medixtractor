package AHA.medixtractor.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Composition;

@Repository
public interface CompositionRepository extends ListCrudRepository<Composition, Long> {

}
