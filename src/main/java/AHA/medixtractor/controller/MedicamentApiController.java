package AHA.medixtractor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.service.MediacamentService;

@RestController
@RequestMapping("/api/medicaments")
public class MedicamentApiController {

    private final MediacamentService mediacamentService;

    public MedicamentApiController(MediacamentService mediacamentService) {
        this.mediacamentService = mediacamentService;
    }

    @GetMapping
    public List<Medicament> getAllMedicaments() {
        return mediacamentService.getMedicaments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicament> getById(@PathVariable Long id) {
        return mediacamentService.getMedicamentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    

    @PostMapping("/import")
    public ResponseEntity<Void> importFromJson(@RequestBody String jsonPayload) {
        
        mediacamentService.createMedicamentsFromJSON(jsonPayload);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidPayload(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}