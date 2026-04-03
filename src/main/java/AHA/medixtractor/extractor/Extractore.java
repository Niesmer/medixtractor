package AHA.medixtractor.extractor;

import java.util.List;

import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.repository.MedicamentRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class Extractore {
    private final MedicamentRepository medicamentRepository;
    private final ObjectMapper objectMapper;


    
    public Extractore(MedicamentRepository medicamentRepository, ObjectMapper objectMapper) {
        this.medicamentRepository = medicamentRepository;
        this.objectMapper = objectMapper;

        createMedicamentsFromJSON(transformDataToJson());
    }

    // TODO : Add search data from gov data that go fetch the textfile
    public String scrapeData(){
        String data; // ???
        return data;
    }

    // TODO : Add function that convert scraped data to a proper JSON string then return Json object in java
    public JsonNode transformDataToJson(){
        String rawText = scrapeData();

        // TODO : Add method that rewrite raw text to JSON
        String converted; //???

        // Convert the rewritten json to a JsonNode object
        JsonNode json = objectMapper.readTree(converted);

        return json;
    }

    public void createMedicamentsFromJSON(JsonNode json) {

        if (json.isArray()) {
            List<Medicament> medicaments = objectMapper.readerForListOf(Medicament.class).readValue(json);
            medicamentRepository.saveAll(medicaments);
            return;
        }

        Medicament medicament = objectMapper.treeToValue(json, Medicament.class);
        medicamentRepository.save(medicament);
    }
}