package AHA.medixtractor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import AHA.medixtractor.model.Presentation;
import AHA.medixtractor.repository.PresentationRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class PresentationService {
    private final PresentationRepository presentationRepository;
    private final ObjectMapper objectMapper;


    public PresentationService(PresentationRepository presentationRepository, ObjectMapper objectMapper) {
        this.presentationRepository = presentationRepository;
        this.objectMapper = objectMapper;
    }

    public Optional<Presentation> getPresentationById(String id){
       return presentationRepository.findById(id);
    }

    public List<Presentation> getPresentations(){
        return presentationRepository.findAll();
    }

    public List<Presentation> filterPresentations(){
        return presentationRepository.findAll();
    }

    // TODO : Maybe change implementation depending on extractor data
    

    public void createPresentationsFromJSON(String json) {
        JsonNode root = objectMapper.readTree(json);

        if (root.isArray()) {
            List<Presentation> presentations = objectMapper.readerForListOf(Presentation.class).readValue(root);
            presentationRepository.saveAll(presentations);
            return;
        }

        Presentation presentation = objectMapper.treeToValue(root, Presentation.class);
        presentationRepository.save(presentation);
    }
}