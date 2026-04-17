package AHA.medixtractor;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import AHA.medixtractor.config.BdpmProperties;
import AHA.medixtractor.config.InseeProperties;
import AHA.medixtractor.config.DatabaseBootstrap;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableConfigurationProperties({BdpmProperties.class, InseeProperties.class})
public class MedixtractorApplication {

	public static void main(String[] args) {
		try {
			// Load .env file if it exists
			Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
			dotenv.entries().forEach(entry -> {
				if (System.getenv(entry.getKey()) == null) {
					System.setProperty(entry.getKey(), entry.getValue());
				}
			});

			Path databaseDir = Path.of("database");
			Path databasePath = databaseDir.resolve("bdpm.db");
			Files.createDirectories(databaseDir);
			Files.createDirectories(Path.of("data", "bdpm"));
			DatabaseBootstrap.ensureCompatibleSqliteSchema(databasePath);
		} catch (java.io.IOException e) {
			throw new UncheckedIOException("Unable to create database directory", e);
		}
		SpringApplication.run(MedixtractorApplication.class, args);
	}

}
