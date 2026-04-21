package AHA.medixtractor.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import AHA.medixtractor.dto.MedicamentSummaryView;
import AHA.medixtractor.model.Composition;
import AHA.medixtractor.model.Favorite;
import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.FavoriteRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.UserRepository;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MedicamentRepository medicamentRepository;
    private final CompositionRepository compositionRepository;

    public FavoriteService(
        FavoriteRepository favoriteRepository,
        UserRepository userRepository,
        MedicamentRepository medicamentRepository,
        CompositionRepository compositionRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.medicamentRepository = medicamentRepository;
        this.compositionRepository = compositionRepository;
    }

    @Transactional(readOnly = true)
    public List<Long> getFavoriteCis(String bearerToken) {
        User user = requireUser(bearerToken);
        return favoriteRepository.findByUserIdOrderByCisAsc(user.getId())
            .stream()
            .map(Favorite::getCis)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<MedicamentSummaryView> getFavorites(String bearerToken) {
        User user = requireUser(bearerToken);
        List<Long> cisValues = favoriteRepository.findByUserIdOrderByCisAsc(user.getId())
            .stream()
            .map(Favorite::getCis)
            .toList();

        if (cisValues.isEmpty()) {
            return List.of();
        }

        List<Medicament> medicaments = medicamentRepository.findAllById(cisValues);
        Map<Long, Medicament> medicamentsByCis = new LinkedHashMap<>();
        for (Medicament medicament : medicaments) {
            medicamentsByCis.put(medicament.getCis(), medicament);
        }

        Map<Long, List<String>> substancesByCis = loadSubstancesByCis(cisValues);

        return cisValues.stream()
            .map(medicamentsByCis::get)
            .filter(java.util.Objects::nonNull)
            .map(medicament -> toSummary(medicament, substancesByCis.getOrDefault(medicament.getCis(), List.of())))
            .toList();
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(String bearerToken, Long cis) {
        User user = requireUser(bearerToken);
        return favoriteRepository.existsByUserIdAndCis(user.getId(), cis);
    }

    @Transactional
    public void addFavorite(String bearerToken, Long cis) {
        User user = requireUser(bearerToken);
        medicamentRepository.findById(cis)
            .orElseThrow(() -> new IllegalArgumentException("Medicament introuvable pour le CIS " + cis));

        if (favoriteRepository.existsByUserIdAndCis(user.getId(), cis)) {
            return;
        }

        favoriteRepository.save(Favorite.builder()
            .userId(user.getId())
            .cis(cis)
            .build());
    }

    @Transactional
    public void removeFavorite(String bearerToken, Long cis) {
        User user = requireUser(bearerToken);
        favoriteRepository.deleteByUserIdAndCis(user.getId(), cis);
    }

    private User requireUser(String bearerToken) {
        String token = extractBearerToken(bearerToken);
        return userRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Token invalide"));
    }

    private String extractBearerToken(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            throw new IllegalArgumentException("Authorization header manquant");
        }
        if (!bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header invalide");
        }
        String token = bearerToken.substring(7).trim();
        if (token.isBlank()) {
            throw new IllegalArgumentException("Token vide");
        }
        return token;
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
}
