package AHA.medixtractor.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Composition;

@Repository
public interface CompositionRepository extends JpaRepository<Composition, Long> {

    List<Composition> findByCisOrderBySubstanceAsc(Long cis);

    List<Composition> findByCisInOrderByCisAscSubstanceAsc(Collection<Long> cisValues);

    @Query(value = """
        select distinct substance
        from composition
        where substance is not null and trim(substance) <> ''
        order by substance asc
        """, nativeQuery = true)
    List<String> findDistinctSubstances();

    @Query(value = """
        select distinct c.substance
        from composition c
        join medicament m on m.cis = c.cis
        where c.substance is not null
          and trim(c.substance) <> ''
          and (:query is null
               or lower(m.nom) like lower('%' || :query || '%')
               or lower(m.laboratoire) like lower('%' || :query || '%')
               or exists (
                   select 1 from composition c2
                   where c2.cis = m.cis
                     and lower(c2.substance) like lower('%' || :query || '%')
               ))
          and (:forme is null or lower(m.forme) = lower(:forme))
          and (:statut is null or lower(m.statut) = lower(:statut))
          and (
              :rembourse is null
              or (lower(:rembourse) = 'oui' and exists (
                  select 1 from presentation p
                  where p.cis = m.cis
                    and p.remboursement is not null
                    and trim(p.remboursement) <> ''
                    and lower(trim(p.remboursement)) not in ('non', 'non remboursable', 'nr')
                    and trim(p.remboursement) not like '0%'
              ))
              or (lower(:rembourse) = 'non' and not exists (
                  select 1 from presentation p
                  where p.cis = m.cis
                    and p.remboursement is not null
                    and trim(p.remboursement) <> ''
                    and lower(trim(p.remboursement)) not in ('non', 'non remboursable', 'nr')
                    and trim(p.remboursement) not like '0%'
              ))
          )
          and (:laboratoire is null or lower(m.laboratoire) = lower(:laboratoire))
        order by c.substance asc
        """, nativeQuery = true)
    List<String> findCompatibleSubstances(
        @Param("query") String query,
        @Param("forme") String forme,
        @Param("statut") String statut,
        @Param("rembourse") String rembourse,
        @Param("laboratoire") String laboratoire
    );
}
