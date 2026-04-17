package AHA.medixtractor.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;
import AHA.medixtractor.config.InseeProperties;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class InseeValidationService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final InseeProperties inseeProperties;

    public InseeValidationService(InseeProperties inseeProperties) {
        this.inseeProperties = inseeProperties;
    }

    public InseeValidationResult validateSiretSiren(String siretSiren, String role) {
        if (siretSiren == null || siretSiren.isBlank()) {
            return new InseeValidationResult(false, role + " must provide SIRET/SIREN number");
        }

        siretSiren = siretSiren.trim();

        // Validate format: SIREN are 9 digits, SIRET are 14 digits
        int expectedLength = role.equals("DOCTOR") ? 9 : 14;
        if (!siretSiren.matches("\\d+")) {
            return new InseeValidationResult(false, "SIRET/SIREN must contain only digits");
        }

        if (siretSiren.length() != expectedLength) {
            return new InseeValidationResult(false,
                role.equals("DOCTOR")
                    ? "SIREN must be exactly 9 digits"
                    : "SIRET must be exactly 14 digits");
        }

        // If API key is not configured, allow signup with warning
        if (inseeProperties.key() == null || inseeProperties.key().isBlank()) {
            return new InseeValidationResult(true,
                "Note: INSEE API key not configured. Accepting SIRET/SIREN without verification.");
        }

        try {
            // Build the API endpoint
            String endpoint = role.equals("DOCTOR")
                ? "/siren/" + siretSiren
                : "/siret/" + siretSiren;

            String url = inseeProperties.url() + endpoint;

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .header("X-INSEE-Api-Key-Integration", inseeProperties.key())
                .timeout(java.time.Duration.ofSeconds(5))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                // For SIREN endpoint, the response has "uniteLegale"
                // For SIRET endpoint, the response has "etablissement"
                if (role.equals("DOCTOR")) {
                    // SIREN validation
                    if (json.has("uniteLegale")) {
                        JsonObject unite = json.getAsJsonObject("uniteLegale");
                        String denominationUniteLegale = "";
                        if (unite.has("denominationUniteLegale")) {
                            denominationUniteLegale = unite.get("denominationUniteLegale").getAsString();
                        }
                        return new InseeValidationResult(true, "Valid DOCTOR registration: " + denominationUniteLegale);
                    }
                } else {
                    // SIRET validation for PHARMACIST
                    if (json.has("etablissement")) {
                        JsonObject etablissement = json.getAsJsonObject("etablissement");
                        String denomination = "";
                        if (etablissement.has("enseigne")) {
                            denomination = etablissement.get("enseigne").getAsString();
                        }
                        if (denomination.isBlank() && etablissement.has("denominationUsuelleEtablissement")) {
                            denomination = etablissement.get("denominationUsuelleEtablissement").getAsString();
                        }

                        // Check NAF code for pharmacy (4773)
                        if (etablissement.has("activitePrincipaleEtablissement")) {
                            String nafCode = etablissement.get("activitePrincipaleEtablissement").getAsString();
                            if (!nafCode.startsWith("4773")) {
                                return new InseeValidationResult(false,
                                    "SIRET must belong to a pharmacy (NAF 4773), found: " + nafCode);
                            }
                        }

                        return new InseeValidationResult(true, "Valid PHARMACIST registration: " + denomination);
                    }
                }

                return new InseeValidationResult(false, "Invalid enterprise information from INSEE");
            } else if (response.statusCode() == 404) {
                return new InseeValidationResult(false, "SIRET/SIREN not found in INSEE database");
            } else if (response.statusCode() == 401) {
                return new InseeValidationResult(false, "INSEE API authentication failed - check API key");
            } else {
                return new InseeValidationResult(false, "INSEE API error (status " + response.statusCode() + ")");
            }
        } catch (java.net.ConnectException | java.net.SocketTimeoutException e) {
            // If INSEE API is unreachable, allow signup with warning
            return new InseeValidationResult(true,
                "Note: Could not verify with INSEE (API unavailable). Please ensure your SIRET/SIREN is correct.");
        } catch (Exception e) {
            return new InseeValidationResult(false, "Error validating SIRET/SIREN: " + e.getMessage());
        }
    }

    public record InseeValidationResult(boolean valid, String message) {}
}
