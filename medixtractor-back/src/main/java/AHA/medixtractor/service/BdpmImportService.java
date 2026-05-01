package AHA.medixtractor.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import AHA.medixtractor.dto.BdpmImportResponse;
import AHA.medixtractor.dto.DatabaseStatusResponse;
import AHA.medixtractor.dto.ImportFileReport;
import AHA.medixtractor.model.Composition;
import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.model.Presentation;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.PresentationRepository;

@Service
public class BdpmImportService {

    private static final Charset BDPM_CHARSET = Charset.forName("windows-1252");
    private static final Pattern DOSAGE_PATTERN = Pattern.compile("^([0-9]+(?:[.,][0-9]+)?)\\s*(.*)$");

    private static final String MEDICAMENT_FILE = "CIS_bdpm.txt";
    private static final String PRESENTATION_FILE = "CIS_CIP_bdpm.txt";
    private static final String COMPOSITION_FILE = "CIS_COMPO_bdpm.txt";

    private final MedicamentRepository medicamentRepository;
    private final PresentationRepository presentationRepository;
    private final CompositionRepository compositionRepository;

    public BdpmImportService(
        MedicamentRepository medicamentRepository,
        PresentationRepository presentationRepository,
        CompositionRepository compositionRepository
    ) {
        this.medicamentRepository = medicamentRepository;
        this.presentationRepository = presentationRepository;
        this.compositionRepository = compositionRepository;
    }

    @Transactional
    public BdpmImportResponse importFromDirectory(Path sourceDir) {
        Path medicamentPath = sourceDir.resolve(MEDICAMENT_FILE);
        Path presentationPath = sourceDir.resolve(PRESENTATION_FILE);
        Path compositionPath = sourceDir.resolve(COMPOSITION_FILE);

        requireFile(medicamentPath);
        requireFile(presentationPath);
        requireFile(compositionPath);

        MedicamentFileParseResult medicamentResult = parseMedicaments(medicamentPath);
        PresentationFileParseResult presentationResult = parsePresentations(presentationPath, medicamentResult.medicaments().keySet());
        CompositionFileParseResult compositionResult = parseCompositions(compositionPath, medicamentResult.medicaments().keySet());

        if (medicamentResult.medicaments().isEmpty()) {
            throw new IllegalArgumentException("Import annule : aucun medicament valide n'a ete detecte dans " + MEDICAMENT_FILE + ".");
        }

        compositionRepository.deleteAllInBatch();
        presentationRepository.deleteAllInBatch();
        medicamentRepository.deleteAllInBatch();

        medicamentRepository.saveAllAndFlush(medicamentResult.medicaments().values());
        presentationRepository.saveAllAndFlush(presentationResult.presentations());
        compositionRepository.saveAllAndFlush(compositionResult.compositions());

        DatabaseStatusResponse statutBase = getDatabaseStatus();

        boolean succes = statutBase.medicaments() == medicamentResult.medicaments().size()
            && statutBase.presentations() == presentationResult.presentations().size()
            && statutBase.compositions() == compositionResult.compositions().size();

        String message = succes
            ? "Import termine avec succes et verifie en base de donnees."
            : "Import partiel : les donnees lues et les donnees presentes en base ne correspondent pas.";

        return new BdpmImportResponse(
            succes,
            message,
            sourceDir.toString(),
            medicamentResult.medicaments().size(),
            presentationResult.presentations().size(),
            compositionResult.compositions().size(),
            statutBase.medicaments(),
            statutBase.presentations(),
            statutBase.compositions(),
            medicamentResult.report(),
            presentationResult.report(),
            compositionResult.report()
        );
    }

    @Transactional(readOnly = true)
    public DatabaseStatusResponse getDatabaseStatus() {
        return new DatabaseStatusResponse(
            medicamentRepository.count(),
            presentationRepository.count(),
            compositionRepository.count()
        );
    }

    private MedicamentFileParseResult parseMedicaments(Path path) {
        Map<Long, Medicament> medicaments = new LinkedHashMap<>();
        int lignesLues = 0;
        int lignesIgnorees = 0;
        int lignesInvalides = 0;

        for (String line : readAllLines(path)) {
            lignesLues++;
            if (line.isBlank()) {
                lignesIgnorees++;
                continue;
            }

            String[] columns = split(line);
            if (columns.length < 11) {
                lignesInvalides++;
                continue;
            }

            Long cis = parseLong(column(columns, 0));
            String nom = column(columns, 1);
            if (cis == null || nom == null) {
                lignesInvalides++;
                continue;
            }

            medicaments.put(cis, Medicament.builder()
                .cis(cis)
                .nom(nom)
                .forme(column(columns, 2))
                .voie(column(columns, 3))
                .statut(column(columns, 4))
                .procedure(column(columns, 5))
                .commercialisation(column(columns, 6))
                .dateAmm(column(columns, 7))
                .laboratoire(column(columns, 10))
                .build());
        }

        return new MedicamentFileParseResult(
            medicaments,
            new ImportFileReport(MEDICAMENT_FILE, lignesLues, medicaments.size(), lignesIgnorees, lignesInvalides)
        );
    }

    private PresentationFileParseResult parsePresentations(Path path, java.util.Set<Long> cisConnus) {
        List<Presentation> presentations = new ArrayList<>();
        int lignesLues = 0;
        int lignesIgnorees = 0;
        int lignesInvalides = 0;

        for (String line : readAllLines(path)) {
            lignesLues++;
            if (line.isBlank()) {
                lignesIgnorees++;
                continue;
            }

            String[] columns = split(line);
            if (columns.length < 10) {
                lignesInvalides++;
                continue;
            }

            Long cis = parseLong(column(columns, 0));
            String cip = column(columns, 1);
            if (cis == null || cip == null || !cisConnus.contains(cis)) {
                lignesInvalides++;
                continue;
            }

            presentations.add(Presentation.builder()
                .cis(cis)
                .cip(cip)
                .prix(parseDouble(column(columns, 9)))
                .remboursement(column(columns, 8))
                .build());
        }

        return new PresentationFileParseResult(
            presentations,
            new ImportFileReport(PRESENTATION_FILE, lignesLues, presentations.size(), lignesIgnorees, lignesInvalides)
        );
    }

    private CompositionFileParseResult parseCompositions(Path path, java.util.Set<Long> cisConnus) {
        List<Composition> compositions = new ArrayList<>();
        int lignesLues = 0;
        int lignesIgnorees = 0;
        int lignesInvalides = 0;

        for (String line : readAllLines(path)) {
            lignesLues++;
            if (line.isBlank()) {
                lignesIgnorees++;
                continue;
            }

            String[] columns = split(line);
            if (columns.length < 5) {
                lignesInvalides++;
                continue;
            }

            Long cis = parseLong(column(columns, 0));
            String substance = column(columns, 3);
            if (cis == null || substance == null || !cisConnus.contains(cis)) {
                lignesInvalides++;
                continue;
            }

            String rawDosage = column(columns, 4);
            ParsedDosage dosage = parseDosage(rawDosage);
            compositions.add(Composition.builder()
                .cis(cis)
                .substance(substance)
                .dosage(dosage.value())
                .unite(dosage.unit())
                .build());
        }

        return new CompositionFileParseResult(
            compositions,
            new ImportFileReport(COMPOSITION_FILE, lignesLues, compositions.size(), lignesIgnorees, lignesInvalides)
        );
    }

    private ParsedDosage parseDosage(String rawDosage) {
        if (rawDosage == null || rawDosage.isBlank()) {
            return new ParsedDosage(null, null);
        }

        Matcher matcher = DOSAGE_PATTERN.matcher(rawDosage.trim());
        if (!matcher.matches()) {
            return new ParsedDosage(null, rawDosage.trim());
        }

        Double value = parseDouble(matcher.group(1).replace(',', '.'));
        String unit = matcher.group(2).isBlank() ? null : matcher.group(2).trim();
        return new ParsedDosage(value, unit);
    }

    private void requireFile(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Fichier BDPM introuvable : " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Le chemin indique n'est pas un fichier : " + path);
        }
    }

    private List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path, BDPM_CHARSET);
        } catch (IOException exception) {
            throw new UncheckedIOException("Lecture impossible du fichier " + path, exception);
        }
    }

    private String[] split(String line) {
        return line.split("\\t", -1);
    }

    private String column(String[] columns, int index) {
        if (index >= columns.length) {
            return null;
        }
        String value = columns[index].trim();
        return value.isBlank() ? null : value;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(normalizeNumericValue(value));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String normalizeNumericValue(String rawValue) {
        String cleaned = rawValue
            .replace("\u00A0", "")
            .replace(" ", "")
            .replace("EUR", "")
            .replace("€", "")
            .trim();

        int lastComma = cleaned.lastIndexOf(',');
        int lastDot = cleaned.lastIndexOf('.');
        int decimalIndex = Math.max(lastComma, lastDot);

        if (decimalIndex < 0) {
            return cleaned.replace(",", "").replace(".", "");
        }

        String integerPart = cleaned.substring(0, decimalIndex).replace(",", "").replace(".", "");
        String decimalPart = cleaned.substring(decimalIndex + 1).replace(",", "").replace(".", "");
        return integerPart + "." + decimalPart;
    }

    private record ParsedDosage(Double value, String unit) {
    }

    private record MedicamentFileParseResult(Map<Long, Medicament> medicaments, ImportFileReport report) {
    }

    private record PresentationFileParseResult(List<Presentation> presentations, ImportFileReport report) {
    }

    private record CompositionFileParseResult(List<Composition> compositions, ImportFileReport report) {
    }
}
