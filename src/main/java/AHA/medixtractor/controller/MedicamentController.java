package AHA.medixtractor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import AHA.medixtractor.dto.MedicamentDetailView;
import AHA.medixtractor.dto.MedicamentSummaryView;
import AHA.medixtractor.dto.SearchFiltersDto;
import AHA.medixtractor.service.MedicamentQueryService;

@RestController
@RequestMapping("/api")
public class MedicamentController {

    private final MedicamentQueryService medicamentQueryService;

    public MedicamentController(MedicamentQueryService medicamentQueryService) {
        this.medicamentQueryService = medicamentQueryService;
    }

    @GetMapping("/medicaments")
    public List<MedicamentSummaryView> search(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String substance,
        @RequestParam(required = false) String forme,
        @RequestParam(required = false) String statut,
        @RequestParam(required = false) String laboratoire
    ) {
        return medicamentQueryService.search(query, substance, forme, statut, laboratoire);
    }

    @GetMapping("/medicaments/{cis}")
    public MedicamentDetailView detail(@PathVariable Long cis) {
        return medicamentQueryService.getDetail(cis);
    }

    @GetMapping("/filters")
    public SearchFiltersDto filters() {
        return medicamentQueryService.getFilters();
    }

    @GetMapping("/filters/compatibles")
    public SearchFiltersDto compatibleFilters(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String substance,
        @RequestParam(required = false) String forme,
        @RequestParam(required = false) String statut,
        @RequestParam(required = false) String laboratoire
    ) {
        return medicamentQueryService.getCompatibleFilters(query, substance, forme, statut, laboratoire);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(IllegalArgumentException exception) {
        return exception.getMessage();
    }
}
