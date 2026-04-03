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

import AHA.medixtractor.model.Presentation;
import AHA.medixtractor.service.PresentationService;

@RestController
@RequestMapping("/api/presentations")
public class PresentationApiController {

    private final PresentationService presentationService;

    public PresentationApiController(PresentationService presentationService) {
        this.presentationService = presentationService;
    }

    @GetMapping
    public List<Presentation> getAllPresentations() {
        return presentationService.getPresentations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presentation> getById(@PathVariable String id) {
        return presentationService.getPresentationById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    

    @PostMapping("/import")
    public ResponseEntity<Void> importFromJson(@RequestBody String jsonPayload) {
        
        presentationService.createPresentationsFromJSON(jsonPayload);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidPayload(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}