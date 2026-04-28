package bo.edu.uagrm.ugram.emr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Snippet — Reusable text macros for rapid clinical data entry.
 * Doctors can type "/gripe" to autocomplete a standard treatment plan (US-W08).
 */
@Entity
@Table(name = "snippets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Snippet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The trigger text, e.g. "gripe", "fractura_simple" */
    @Column(nullable = false, unique = true)
    private String trigger;

    /** Human-readable name for the snippet */
    @Column(nullable = false)
    private String name;

    /** The full text content that replaces the trigger */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Category for organization (e.g. "tratamiento", "diagnóstico") */
    private String category;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
