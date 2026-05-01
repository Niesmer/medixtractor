package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import AHA.medixtractor.config.InseeProperties;

class InseeValidationServiceValidateSiretSirenTest {

    @Test
    void validateSiretSirenRejectsBlankValue() {
        InseeValidationService service = new InseeValidationService(new InseeProperties(null, "https://example.test"));

        InseeValidationService.InseeValidationResult result = service.validateSiretSiren(" ", "DOCTOR");

        assertFalse(result.valid());
        assertEquals("DOCTOR must provide SIRET/SIREN number", result.message());
    }

    @Test
    void validateSiretSirenRejectsNonDigits() {
        InseeValidationService service = new InseeValidationService(new InseeProperties(null, "https://example.test"));

        InseeValidationService.InseeValidationResult result = service.validateSiretSiren("123ABC789", "DOCTOR");

        assertFalse(result.valid());
        assertEquals("SIRET/SIREN must contain only digits", result.message());
    }

    @Test
    void validateSiretSirenAcceptsDoctorSirenWhenApiKeyIsMissing() {
        InseeValidationService service = new InseeValidationService(new InseeProperties("", "https://example.test"));

        InseeValidationService.InseeValidationResult result = service.validateSiretSiren("123456789", "DOCTOR");

        assertTrue(result.valid());
        assertEquals("Note: INSEE API key not configured. Accepting SIRET/SIREN without verification.", result.message());
    }

    @Test
    void validateSiretSirenRequiresFourteenDigitsForPharmacist() {
        InseeValidationService service = new InseeValidationService(new InseeProperties(null, "https://example.test"));

        InseeValidationService.InseeValidationResult result = service.validateSiretSiren("123456789", "PHARMACIST");

        assertFalse(result.valid());
        assertEquals("SIRET must be exactly 14 digits", result.message());
    }
}
