package bo.edu.uagrm.ugram.laboratory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Lab Catalog — Master catalog of available laboratory tests.
 * Managed by ADMIN role.
 */
@Entity
@Table(name = "lab_catalogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(name = "turnaround_time_desc")
    private String turnaroundTimeDesc;

    @Column(name = "reference_range")
    private String referenceRange;

    @Column(name = "unit")
    private String unit;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
