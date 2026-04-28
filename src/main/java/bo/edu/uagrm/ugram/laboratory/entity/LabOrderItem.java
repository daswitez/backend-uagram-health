package bo.edu.uagrm.ugram.laboratory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Lab Order Item — Individual test within a lab order.
 * Contains the result value and flag once processed.
 */
@Entity
@Table(name = "lab_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder labOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id", nullable = false)
    private LabCatalog catalog;

    @Column(name = "result_value")
    private String resultValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_flag")
    private ResultFlag resultFlag;

    /** Path to uploaded file in MinIO (PDF, DICOM, etc.) */
    @Column(name = "file_url")
    private String fileUrl;
}
