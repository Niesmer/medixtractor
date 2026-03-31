package AHA.medixtractor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.repository.MedicamentRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class MediacamentService {
    private final MedicamentRepository medicamentRepository;
    private final ObjectMapper objectMapper;


    public MediacamentService(MedicamentRepository medicamentRepository, ObjectMapper objectMapper) {
        this.medicamentRepository = medicamentRepository;
        this.objectMapper = objectMapper;
    }

    public void createMedicament(Medicament medicament){
        medicamentRepository.save(medicament);
    }
    

    public Optional<Medicament> getMedicamentById(Long id){
       return medicamentRepository.findById(id);
    }

    public List<Medicament> getMedicaments(){
        return medicamentRepository.findAll();
    }

    // TODO : Maybe change implementation depending on extractor data
    

    public void createMedicamentsFromJSON(String json) {
        JsonNode root = objectMapper.readTree(json);

        if (root.isArray()) {
            List<Medicament> medicaments = objectMapper.readerForListOf(Medicament.class).readValue(root);
            medicamentRepository.saveAll(medicaments);
            return;
        }

        Medicament medicament = objectMapper.treeToValue(root, Medicament.class);
        medicamentRepository.save(medicament);
    }
}