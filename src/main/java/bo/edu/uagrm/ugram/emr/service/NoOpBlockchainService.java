package bo.edu.uagrm.ugram.emr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * No-Op Blockchain Service — Placeholder for Phases 1-3.
 *
 * Logs the hash that would be anchored to the blockchain and returns
 * a mock transaction ID. Will be replaced by HyperledgerBlockchainService
 * in Phase 4.
 *
 * Annotated with @Service so it's the default implementation.
 * When the real implementation is ready, use @Primary or @Profile
 * to switch.
 */
@Service
public class NoOpBlockchainService implements BlockchainService {

    private static final Logger log = LoggerFactory.getLogger(NoOpBlockchainService.class);

    @Override
    public String anchorHash(UUID patientId, UUID doctorId, String contentHash) {
        String mockTxId = "noop-tx-" + UUID.randomUUID();
        log.info("[BLOCKCHAIN-NOOP] Anchoring hash for patient={}, doctor={}, hash={}, mockTxId={}",
                patientId, doctorId, contentHash, mockTxId);
        return mockTxId;
    }

    @Override
    public boolean verifyHash(String blockchainTxId, String contentHash) {
        log.info("[BLOCKCHAIN-NOOP] Verifying hash txId={}, hash={} — always returns true",
                blockchainTxId, contentHash);
        return true;
    }
}
