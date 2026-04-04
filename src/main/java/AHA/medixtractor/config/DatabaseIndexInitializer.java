package AHA.medixtractor.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseIndexInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseIndexInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("create index if not exists idx_medicament_nom on medicament(nom)");
        jdbcTemplate.execute("create index if not exists idx_medicament_forme on medicament(forme)");
        jdbcTemplate.execute("create index if not exists idx_medicament_statut on medicament(statut)");
        jdbcTemplate.execute("create index if not exists idx_composition_cis on composition(cis)");
        jdbcTemplate.execute("create index if not exists idx_composition_substance on composition(substance)");
        jdbcTemplate.execute("create index if not exists idx_presentation_cis on presentation(cis)");
    }
}
