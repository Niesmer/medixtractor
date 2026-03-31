package AHA.medixtractor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Presentation;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, String> {

}
