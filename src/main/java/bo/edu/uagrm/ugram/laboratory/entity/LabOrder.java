package bo.edu.uagrm.ugram.laboratory.entity;

import bo.edu.uagrm.ugram.identity.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Lab Order — A request for one or more lab tests.
 * Created by doctors from the EMR (US-W09), managed by lab techs (US-W11/W12).
 */
@Entity
@Table(name = "lab_orders", indexes = {
    @Index(name = "idx_lab_order_status", columnList = "status"),
    @Index(name = "idx_lab_order_patient", columnList = "patient_id"),
    @Index(name = "idx_lab_order_priority", columnList = "priority")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordered_by", nullable = false)
    private User orderedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LabOrderStatus status = LabOrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LabPriority priority = LabPriority.ROUTINE;

    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    @OneToMany(mappedBy = "labOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LabOrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void addItem(LabOrderItem item) {
        items.add(item);
        item.setLabOrder(this);
    }
}
