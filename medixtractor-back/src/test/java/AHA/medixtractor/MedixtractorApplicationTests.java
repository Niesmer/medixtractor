package AHA.medixtractor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class MedixtractorApplicationTests {

    private static final Charset BDPM_CHARSET = Charset.forName("windows-1252");

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void importsBdpmAndSearchesBySubstance() throws Exception {
        Path sourceDir = Files.createDirectories(tempDir.resolve("bdpm"));
        write(sourceDir.resolve("CIS_bdpm.txt"),
            "12345678\tDOLIPRANE 1000 mg, comprime\tcomprime\torale\tAMM\tNationale\tCommercialise\t01/01/2020\tDisponible\tEU/123\tSANOFI\tNon",
            "87654321\tIBUPROFENE BIOGARAN 400 mg, comprime\tcomprime\torale\tAMM\tNationale\tCommercialise\t05/02/2021\tDisponible\tEU/456\tBIOGARAN\tNon"
        );
        write(sourceDir.resolve("CIS_CIP_bdpm.txt"),
            "12345678\t1111111\tBoite de 8 comprimes\tActif\tCommercialise\t01/01/2020\t3400930001111\tOui\t65%\t2.18\tRemboursable",
            "87654321\t2222222\tBoite de 12 comprimes\tActif\tCommercialise\t05/02/2021\t3400930002222\tOui\t30%\t3.50\tRemboursable"
        );
        write(sourceDir.resolve("CIS_COMPO_bdpm.txt"),
            "12345678\tPrincipe actif\t1111\tPARACETAMOL\t1000 mg\tcomprime\tSA\t1",
            "87654321\tPrincipe actif\t2222\tIBUPROFENE\t400 mg\tcomprime\tSA\t1"
        );

        mockMvc.perform(post("/api/imports/bdpm").param("sourceDir", sourceDir.toString()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.succes").value(true))
            .andExpect(jsonPath("$.message").value("Import termine avec succes et verifie en base de donnees."))
            .andExpect(jsonPath("$.medicamentsImported").value(2))
            .andExpect(jsonPath("$.presentationsImported").value(2))
            .andExpect(jsonPath("$.compositionsImported").value(2))
            .andExpect(jsonPath("$.medicamentsEnBase").value(2))
            .andExpect(jsonPath("$.presentationsEnBase").value(2))
            .andExpect(jsonPath("$.compositionsEnBase").value(2))
            .andExpect(jsonPath("$.fichierMedicaments.lignesImportees").value(2))
            .andExpect(jsonPath("$.fichierPresentations.lignesImportees").value(2))
            .andExpect(jsonPath("$.fichierCompositions.lignesImportees").value(2));

        mockMvc.perform(get("/api/imports/statut"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.medicaments").value(2))
            .andExpect(jsonPath("$.presentations").value(2))
            .andExpect(jsonPath("$.compositions").value(2));

        mockMvc.perform(get("/api/imports/demarrage"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tentativeEffectuee").exists())
            .andExpect(jsonPath("$.succes").exists())
            .andExpect(jsonPath("$.message").exists());

        mockMvc.perform(get("/api/medicaments").param("substance", "PARACETAMOL"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].cis").value(12345678L))
            .andExpect(jsonPath("$[0].name").value("DOLIPRANE 1000 mg, comprime"))
            .andExpect(jsonPath("$[0].activeSubstances[0]").value("PARACETAMOL"));

        mockMvc.perform(get("/api/filters"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.substances[0]").value("IBUPROFENE"))
            .andExpect(jsonPath("$.substances[1]").value("PARACETAMOL"))
            .andExpect(jsonPath("$.formes[0]").value("comprime"));

        mockMvc.perform(get("/api/medicaments/12345678"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.laboratory").value("SANOFI"))
            .andExpect(jsonPath("$.compositions[0].substance").value("PARACETAMOL"))
            .andExpect(jsonPath("$.presentations[0].cip").value("1111111"));
    }

    private void write(Path path, String... lines) throws IOException {
        Files.write(path, java.util.List.of(lines), BDPM_CHARSET);
    }
}
