package AHA.medixtractor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
            "87654321\tIBUPROFENE BIOGARAN 400 mg, comprime\tcomprime\torale\tAMM\tNationale\tCommercialise\t05/02/2021\tDisponible\tEU/456\tBIOGARAN\tNon",
            "33333333\tZINC TEST 10 mg, comprime\tcomprime\torale\tAMM\tNationale\tCommercialise\t01/01/2022\tDisponible\tEU/789\tTESTLAB\tNon"
        );
        write(sourceDir.resolve("CIS_CIP_bdpm.txt"),
            "12345678\t1111111\tBoite de 8 comprimes\tActif\tCommercialise\t01/01/2020\t3400930001111\tOui\t65%\t2.18\tRemboursable",
            "87654321\t2222222\tBoite de 12 comprimes\tActif\tCommercialise\t05/02/2021\t3400930002222\tOui\t30%\t3.50\tRemboursable",
            "33333333\t3333333\tBoite de 30 comprimes\tActif\tCommercialise\t01/01/2022\t3400930003333\tOui\t0%\t1.00\tNon remboursable"
        );
        write(sourceDir.resolve("CIS_COMPO_bdpm.txt"),
            "12345678\tPrincipe actif\t1111\tPARACETAMOL\t1000 mg\tcomprime\tSA\t1",
            "87654321\tPrincipe actif\t2222\tIBUPROFENE\t400 mg\tcomprime\tSA\t1",
            "33333333\tPrincipe actif\t3333\tZINC\t10 mg\tcomprime\tSA\t1"
        );

        mockMvc.perform(post("/api/imports/bdpm").param("sourceDir", sourceDir.toString()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.succes").value(true))
            .andExpect(jsonPath("$.message").value("Import termine avec succes et verifie en base de donnees."))
            .andExpect(jsonPath("$.medicamentsImported").value(3))
            .andExpect(jsonPath("$.presentationsImported").value(3))
            .andExpect(jsonPath("$.compositionsImported").value(3))
            .andExpect(jsonPath("$.medicamentsEnBase").value(3))
            .andExpect(jsonPath("$.presentationsEnBase").value(3))
            .andExpect(jsonPath("$.compositionsEnBase").value(3))
            .andExpect(jsonPath("$.fichierMedicaments.lignesImportees").value(3))
            .andExpect(jsonPath("$.fichierPresentations.lignesImportees").value(3))
            .andExpect(jsonPath("$.fichierCompositions.lignesImportees").value(3));

        mockMvc.perform(get("/api/imports/statut"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.medicaments").value(3))
            .andExpect(jsonPath("$.presentations").value(3))
            .andExpect(jsonPath("$.compositions").value(3));

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

        mockMvc.perform(get("/api/medicaments").param("rembourse", "oui"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/medicaments").param("rembourse", "non"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].cis").value(33333333L));

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

    @Test
    void managesFavoritesForAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content("""
                    {
                      "email":"favorites@example.com",
                      "password":"secret123",
                      "fullName":"Favoris Test",
                      "role":"ADMIN"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").exists());

        String token = mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("""
                    {
                      "email":"favorites@example.com",
                      "password":"secret123"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String bearer = "Bearer " + extractJsonValue(token, "token");

        Path sourceDir = Files.createDirectories(tempDir.resolve("bdpm-favorites"));
        write(sourceDir.resolve("CIS_bdpm.txt"),
            "12345678\tDOLIPRANE 1000 mg, comprime\tcomprime\torale\tAMM\tNationale\tCommercialise\t01/01/2020\tDisponible\tEU/123\tSANOFI\tNon"
        );
        write(sourceDir.resolve("CIS_CIP_bdpm.txt"),
            "12345678\t1111111\tBoite de 8 comprimes\tActif\tCommercialise\t01/01/2020\t3400930001111\tOui\t65%\t2.18\tRemboursable"
        );
        write(sourceDir.resolve("CIS_COMPO_bdpm.txt"),
            "12345678\tPrincipe actif\t1111\tPARACETAMOL\t1000 mg\tcomprime\tSA\t1"
        );

        mockMvc.perform(post("/api/imports/bdpm").param("sourceDir", sourceDir.toString()))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/favorites/12345678").header("Authorization", bearer))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/favorites/12345678").header("Authorization", bearer))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(true));

        mockMvc.perform(get("/api/favorites/cis").header("Authorization", bearer))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value(12345678L));

        mockMvc.perform(get("/api/favorites").header("Authorization", bearer))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].cis").value(12345678L))
            .andExpect(jsonPath("$[0].activeSubstances[0]").value("PARACETAMOL"));

        mockMvc.perform(delete("/api/favorites/12345678").header("Authorization", bearer))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/favorites/12345678").header("Authorization", bearer))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(false));
    }

    private void write(Path path, String... lines) throws IOException {
        Files.write(path, java.util.List.of(lines), BDPM_CHARSET);
    }

    private String extractJsonValue(String json, String key) {
        String marker = "\"" + key + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            throw new IllegalArgumentException("JSON key not found: " + key);
        }
        int valueStart = start + marker.length();
        int valueEnd = json.indexOf('"', valueStart);
        return json.substring(valueStart, valueEnd);
    }
}
