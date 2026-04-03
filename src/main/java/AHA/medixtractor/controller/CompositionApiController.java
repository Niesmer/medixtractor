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

import AHA.medixtractor.model.Composition;
import AHA.medixtractor.service.CompositionService;

@RestController
@RequestMapping("/api/compositions")
public class CompositionApiController {

    private final CompositionService compositionService;

    public CompositionApiController(CompositionService compositionService) {
        this.compositionService = compositionService;
    }

    @GetMapping
    public List<Composition> getAllCompositions() {
        return compositionService.getCompositions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Composition> getById(@PathVariable Integer id) {
        return compositionService.getCompositionById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    

    @PostMapping("/import")
    public ResponseEntity<Void> importFromJson(@RequestBody String jsonPayload) {
        
        compositionService.createCompositionsFromJSON(jsonPayload);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidPayload(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}