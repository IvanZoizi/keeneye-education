package keenay.education.entity;

import jakarta.persistence.*;
import keenay.education.entity.status.TasksStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "advertisement_id", nullable = true,
            insertable = false, updatable = false)
    private Advertisement advertisement;

    @Column(name = "advertisement_id")
    private Long advertisementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "client_id", insertable = false, updatable = false)
    private Customers customer;

    @Column(name = "client_id")
    private Long customerId;

    @Column(name = "photo", nullable = true)
    private String photoUrl;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TasksStatus status = TasksStatus.CREATED;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
