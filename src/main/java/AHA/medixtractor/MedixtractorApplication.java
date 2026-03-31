package AHA.medixtractor;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedixtractorApplication {

	public static void main(String[] args) {
		try {
			Files.createDirectories(Path.of("database"));
		} catch (java.io.IOException e) {
			throw new UncheckedIOException("Unable to create database directory", e);
		}
		SpringApplication.run(MedixtractorApplication.class, args);
	}

}
