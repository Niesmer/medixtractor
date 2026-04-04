package AHA.medixtractor;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import AHA.medixtractor.config.BdpmProperties;
import AHA.medixtractor.config.DatabaseBootstrap;

@SpringBootApplication
@EnableConfigurationProperties(BdpmProperties.class)
public class MedixtractorApplication {

	public static void main(String[] args) {
		try {
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
