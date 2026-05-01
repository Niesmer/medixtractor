package AHA.medixtractor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ManagesFavoritesForAuthenticatedUserTest extends IntegrationTestSupport {

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

        String loginResponse = mockMvc.perform(post("/api/auth/login")
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

        String bearer = "Bearer " + extractJsonValue(loginResponse, "token");

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
}
