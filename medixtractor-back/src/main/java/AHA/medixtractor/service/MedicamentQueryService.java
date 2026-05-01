package AHA.medixtractor.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import AHA.medixtractor.dto.CompositionView;
import AHA.medixtractor.dto.MedicamentDetailView;
import AHA.medixtractor.dto.MedicamentSummaryView;
import AHA.medixtractor.dto.PresentationView;
import AHA.medixtractor.dto.SearchFiltersDto;
import AHA.medixtractor.model.Composition;
import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.model.Presentation;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.PresentationRepository;

@Service
public class MedicamentQueryService {

    private final MedicamentRepository medicamentRepository;
    private final CompositionRepository compositionRepository;
    private final PresentationRepository presentationRepository;

    public MedicamentQueryService(
        MedicamentRepository medicamentRepository,
        CompositionRepository compositionRepository,
        PresentationRepository presentationRepository
    ) {
        this.medicamentRepository = medicamentRepository;
        this.compositionRepository = compositionRepository;
        this.presentationRepository = presentationRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicamentSummaryView> search(
        String query,
        String substance,
        String forme,
        String statut,
        String rembourse,
        String laboratoire
    ) {
        List<Medicament> medicaments = medicamentRepository.search(
            normalize(query),
            normalize(substance),
            normalize(forme),
            normalize(statut),
            normalize(rembourse),
            normalize(laboratoire)
        );
        Map<Long, List<String>> substancesByCis = loadSubstancesByCis(medicaments.stream().map(Medicament::getCis).toList());

        return medicaments
            .stream()
            .map(medicament -> toSummary(medicament, substancesByCis.getOrDefault(medicament.getCis(), List.of())))
            .toList();
    }

    @Transactional(readOnly = true)
    public MedicamentDetailView getDetail(Long cis) {
        Medicament medicament = medicamentRepository.findById(cis)
            .orElseThrow(() -> new IllegalArgumentException("Medicament introuvable pour le CIS " + cis));

        List<Composition> compositions = compositionRepository.findByCisOrderBySubstanceAsc(cis);
        List<Presentation> presentations = presentationRepository.findByCisOrderByCipAsc(cis);

        return new MedicamentDetailView(
            medicament.getCis(),
            medicament.getCis(),
            medicament.getNom(),
            medicament.getForme(),
            medicament.getVoie(),
            medicament.getStatut(),
            medicament.getProcedure(),
            medicament.getCommercialisation(),
            medicament.getDateAmm(),
            medicament.getLaboratoire(),
            compositions.stream().map(Composition::getSubstance).distinct().toList(),
            compositions.stream().map(CompositionView::fromEntity).toList(),
            presentations.stream().map(PresentationView::fromEntity).toList()
        );
    }

    @Transactional(readOnly = true)
    public SearchFiltersDto getFilters() {
        List<String> substances = compositionRepository.findDistinctSubstances();
        List<String> formes = medicamentRepository.findDistinctFormes();
        List<String> statuts = medicamentRepository.findDistinctStatuts();
        List<String> laboratoires = medicamentRepository.findDistinctLaboratoires();

        return new SearchFiltersDto(substances, formes, statuts, laboratoires);
    }

    @Transactional(readOnly = true)
    public SearchFiltersDto getCompatibleFilters(
        String query,
        String substance,
        String forme,
        String statut,
        String rembourse,
        String laboratoire
    ) {
        String normalizedQuery = normalize(query);
        String normalizedSubstance = normalize(substance);
        String normalizedForme = normalize(forme);
        String normalizedStatut = normalize(statut);
        String normalizedRembourse = normalize(rembourse);
        String normalizedLaboratoire = normalize(laboratoire);

        List<String> substances = compositionRepository.findCompatibleSubstances(
            normalizedQuery,
            normalizedForme,
            normalizedStatut,
            normalizedRembourse,
            normalizedLaboratoire
        );
        List<String> formes = medicamentRepository.findCompatibleFormes(
            normalizedQuery,
            normalizedSubstance,
            normalizedStatut,
            normalizedRembourse,
            normalizedLaboratoire
        );
        List<String> statuts = medicamentRepository.findCompatibleStatuts(
            normalizedQuery,
            normalizedSubstance,
            normalizedForme,
            normalizedRembourse,
            normalizedLaboratoire
        );
        List<String> laboratoires = medicamentRepository.findCompatibleLaboratoires(
            normalizedQuery,
            normalizedSubstance,
            normalizedForme,
            normalizedStatut,
            normalizedRembourse
        );

        return new SearchFiltersDto(substances, formes, statuts, laboratoires);
    }

    private MedicamentSummaryView toSummary(Medicament medicament, List<String> substances) {
        return new MedicamentSummaryView(
            medicament.getCis(),
            medicament.getCis(),
            medicament.getNom(),
            medicament.getForme(),
            medicament.getVoie(),
            medicament.getStatut(),
            medicament.getLaboratoire(),
            substances,
            medicament.getDateAmm()
        );
    }

    private Map<Long, List<String>> loadSubstancesByCis(List<Long> cisValues) {
        if (cisValues.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, List<String>> substancesByCis = new LinkedHashMap<>();
        for (Composition composition : compositionRepository.findByCisInOrderByCisAscSubstanceAsc(cisValues)) {
            if (composition.getSubstance() == null || composition.getSubstance().isBlank()) {
                continue;
            }
            List<String> substances = substancesByCis.computeIfAbsent(composition.getCis(), key -> new java.util.ArrayList<>());
            if (!substances.contains(composition.getSubstance())) {
                substances.add(composition.getSubstance());
            }
        }
        return substancesByCis;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
