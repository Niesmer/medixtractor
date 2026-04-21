package AHA.medixtractor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserIdOrderByCisAsc(Long userId);

    List<Favorite> findByUserIdAndCisInOrderByCisAsc(Long userId, List<Long> cisValues);

    Optional<Favorite> findByUserIdAndCis(Long userId, Long cis);

    boolean existsByUserIdAndCis(Long userId, Long cis);

    void deleteByUserIdAndCis(Long userId, Long cis);
}
