package bo.edu.uagrm.ugram.emr.entity;

import bo.edu.uagrm.ugram.identity.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Correction Note — Append-only amendment to a clinical record.
 *
 * When a doctor makes an error in a clinical record, they cannot modify it.
 * Instead, they create a CorrectionNote that references the original record.
 * This mirrors real-world medical practice (US-B02).
 */
@Entity
@Table(name = "correction_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrectionNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_record_id", nullable = false)
    private ClinicalRecord originalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "correction_content", nullable = false, columnDefinition = "TEXT")
    private String correctionContent;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "blockchain_tx_id")
    private String blockchainTxId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;
}
