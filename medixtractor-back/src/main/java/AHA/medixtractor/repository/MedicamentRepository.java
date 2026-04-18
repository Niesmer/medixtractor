package AHA.medixtractor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import AHA.medixtractor.model.Medicament;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    @Query(value = """
        select *
        from medicament m
        where (:query is null
               or lower(m.nom) like lower('%' || :query || '%')
               or lower(m.laboratoire) like lower('%' || :query || '%')
               or exists (
                   select 1 from composition c
                   where c.cis = m.cis
                     and lower(c.substance) like lower('%' || :query || '%')
               ))
          and (:substance is null
               or exists (
                   select 1 from composition c2
                   where c2.cis = m.cis
                     and lower(c2.substance) = lower(:substance)
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
        order by m.nom asc
        limit 50
        """, nativeQuery = true)
    List<Medicament> search(
        @Param("query") String query,
        @Param("substance") String substance,
        @Param("forme") String forme,
        @Param("statut") String statut,
        @Param("rembourse") String rembourse,
        @Param("laboratoire") String laboratoire
    );

    @Query(value = """
        select distinct forme
        from medicament
        where forme is not null and trim(forme) <> ''
        order by forme asc
        """, nativeQuery = true)
    List<String> findDistinctFormes();

    @Query(value = """
        select distinct statut
        from medicament
        where statut is not null and trim(statut) <> ''
        order by statut asc
        """, nativeQuery = true)
    List<String> findDistinctStatuts();

    @Query(value = """
        select distinct laboratoire
        from medicament
        where laboratoire is not null and trim(laboratoire) <> ''
        order by laboratoire asc
        """, nativeQuery = true)
    List<String> findDistinctLaboratoires();

    @Query(value = """
        select distinct m.forme
        from medicament m
        where m.forme is not null
          and trim(m.forme) <> ''
          and (:query is null
               or lower(m.nom) like lower('%' || :query || '%')
               or lower(m.laboratoire) like lower('%' || :query || '%')
               or exists (
                   select 1 from composition c
                   where c.cis = m.cis
                     and lower(c.substance) like lower('%' || :query || '%')
               ))
          and (:substance is null
               or exists (
                   select 1 from composition c2
                   where c2.cis = m.cis
                     and lower(c2.substance) = lower(:substance)
               ))
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
        order by m.forme asc
        """, nativeQuery = true)
    List<String> findCompatibleFormes(
        @Param("query") String query,
        @Param("substance") String substance,
        @Param("statut") String statut,
        @Param("rembourse") String rembourse,
        @Param("laboratoire") String laboratoire
    );

    @Query(value = """
        select distinct m.statut
        from medicament m
        where m.statut is not null
          and trim(m.statut) <> ''
          and (:query is null
               or lower(m.nom) like lower('%' || :query || '%')
               or lower(m.laboratoire) like lower('%' || :query || '%')
               or exists (
                   select 1 from composition c
                   where c.cis = m.cis
                     and lower(c.substance) like lower('%' || :query || '%')
               ))
          and (:substance is null
               or exists (
                   select 1 from composition c2
                   where c2.cis = m.cis
                     and lower(c2.substance) = lower(:substance)
               ))
          and (:forme is null or lower(m.forme) = lower(:forme))
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
        order by m.statut asc
        """, nativeQuery = true)
    List<String> findCompatibleStatuts(
        @Param("query") String query,
        @Param("substance") String substance,
        @Param("forme") String forme,
        @Param("rembourse") String rembourse,
        @Param("laboratoire") String laboratoire
    );

    @Query(value = """
        select distinct m.laboratoire
        from medicament m
        where m.laboratoire is not null
          and trim(m.laboratoire) <> ''
          and (:query is null
               or lower(m.nom) like lower('%' || :query || '%')
               or lower(m.laboratoire) like lower('%' || :query || '%')
               or exists (
                   select 1 from composition c
                   where c.cis = m.cis
                     and lower(c.substance) like lower('%' || :query || '%')
               ))
          and (:substance is null
               or exists (
                   select 1 from composition c2
                   where c2.cis = m.cis
                     and lower(c2.substance) = lower(:substance)
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
        order by m.laboratoire asc
        """, nativeQuery = true)
    List<String> findCompatibleLaboratoires(
        @Param("query") String query,
        @Param("substance") String substance,
        @Param("forme") String forme,
        @Param("statut") String statut,
        @Param("rembourse") String rembourse
    );
}
