package bo.edu.uagrm.ugram.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Extended profile for students (patients).
 * Maps to the `patients` table.
 */
@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "career")
    private String career;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;
}
