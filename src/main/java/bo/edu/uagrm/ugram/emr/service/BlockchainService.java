package bo.edu.uagrm.ugram.emr.service;

import java.util.UUID;

/**
 * Blockchain Service Interface — Abstraction for EMR hash anchoring.
 *
 * DESIGN DECISION: This is an interface so we can swap implementations:
 * - Phase 1-3: NoOpBlockchainService (logs hash, returns mock TX ID)
 * - Phase 4:   HyperledgerBlockchainService (real Fabric integration)
 *
 * This allows the entire EMR module to be built and tested without
 * Hyperledger Fabric infrastructure being ready.
 */
public interface BlockchainService {

    /**
     * Anchors a clinical record hash to the blockchain ledger.
     *
     * @param patientId the patient UUID
     * @param doctorId  the doctor UUID
     * @param contentHash SHA-256 hash of the clinical content
     * @return the blockchain transaction ID (TX_ID)
     */
    String anchorHash(UUID patientId, UUID doctorId, String contentHash);

    /**
     * Verifies that a content hash exists on the blockchain ledger.
     *
     * @param blockchainTxId the transaction ID to verify
     * @param contentHash    the expected hash
     * @return true if the hash matches the on-chain record
     */
    boolean verifyHash(String blockchainTxId, String contentHash);
}
