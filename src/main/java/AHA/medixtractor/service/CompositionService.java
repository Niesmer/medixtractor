package AHA.medixtractor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import AHA.medixtractor.model.Composition;
import AHA.medixtractor.repository.CompositionRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class CompositionService {
    private final CompositionRepository compositionRepository;
    private final ObjectMapper objectMapper;


    public CompositionService(CompositionRepository compositionRepository, ObjectMapper objectMapper) {
        this.compositionRepository = compositionRepository;
        this.objectMapper = objectMapper;
    }

    public Optional<Composition> getCompositionById(Integer id){
       return compositionRepository.findById(id);
    }

    public List<Composition> getCompositions(){
        return compositionRepository.findAll();
    }

    public List<Composition> filterCompositions(){
        return compositionRepository.findAll();
    }

    // TODO : Maybe change implementation depending on extractor data
    

    public void createCompositionsFromJSON(String json) {
        JsonNode root = objectMapper.readTree(json);

        if (root.isArray()) {
            List<Composition> compositions = objectMapper.readerForListOf(Composition.class).readValue(root);
            compositionRepository.saveAll(compositions);
            return;
        }

        Composition composition = objectMapper.treeToValue(root, Composition.class);
        compositionRepository.save(composition);
    }
}