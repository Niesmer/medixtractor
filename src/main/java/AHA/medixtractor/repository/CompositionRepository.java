package AHA.medixtractor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Composition;

@Repository
public interface CompositionRepository extends JpaRepository<Composition, Integer> {

}
