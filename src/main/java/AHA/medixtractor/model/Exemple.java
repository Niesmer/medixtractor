package AHA.medixtractor.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exemple {

	private Long id;
	private String title;
	private String content;
	private boolean active;
	private LocalDateTime createdAt;

	public static Exemple mock() {
		return Exemple.builder()
				.id(1L)
				.title("Mock title")
				.content("Mock content")
				.active(true)
				.createdAt(LocalDateTime.now())
				.build();
	}
}