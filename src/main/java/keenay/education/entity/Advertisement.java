package keenay.education.entity;

import jakarta.persistence.*;
import keenay.education.entity.status.AdvertisementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "advertisement")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "pet_id", insertable = false, updatable = false)
    private Pets pet;

    @Column( name = "pet_id")
    private Long petId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "client_id", insertable = false, updatable = false)
    private Customers customer;

    @Column(name = "client_id")
    private Long customerId;

    @OneToMany(mappedBy = "advertisement", fetch = FetchType.LAZY)
    private List<AdvertisementResponse> responses = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_response_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AdvertisementResponse selectedResponse;

    @Column(name = "selected_response_id")
    private Long selectedResponseId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AdvertisementStatus status = AdvertisementStatus.CREATED;

    @Column(name = "budget")
    private Integer budget;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "advertisement", fetch = FetchType.LAZY)
    private List<Tasks> tasksList = new ArrayList<>();
}
