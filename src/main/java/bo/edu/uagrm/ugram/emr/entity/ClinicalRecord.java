package bo.edu.uagrm.ugram.emr.entity;

import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.scheduling.entity.Appointment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Clinical Record — The core EMR entity.
 *
 * IMMUTABILITY BY DESIGN:
 * - No `updated_at` column. Records are never modified.
 * - If a correction is needed, a CorrectionNote is created referencing this record.
 * - `content_hash` is a SHA-256 digest verified against the Blockchain ledger.
 * - `encrypted_payload` contains AES-GCM encrypted clinical data.
 *
 * US-W06, US-W07, US-B01, US-B02
 */
@Entity
@Table(name = "clinical_records", indexes = {
    @Index(name = "idx_clinical_patient", columnList = "patient_id"),
    @Index(name = "idx_clinical_appointment", columnList = "appointment_id"),
    @Index(name = "idx_clinical_blockchain_tx", columnList = "blockchain_tx_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    /**
     * AES-GCM encrypted clinical content (anamnesis, diagnosis, treatment plan).
     * Only authorized medical staff can decrypt this.
     */
    @Column(name = "encrypted_payload", columnDefinition = "TEXT", nullable = false)
    private String encryptedPayload;

    /**
     * SHA-256 hash of the original (unencrypted) payload.
     * Used for Blockchain verification.
     */
    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    /**
     * Transaction ID from the Hyperledger Fabric ledger.
     * Null until blockchain confirmation is received.
     */
    @Column(name = "blockchain_tx_id")
    private String blockchainTxId;

    /**
     * Immutable timestamp. No updated_at by design.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;
}
