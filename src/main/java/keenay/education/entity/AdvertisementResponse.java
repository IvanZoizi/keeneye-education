package keenay.education.entity;

import jakarta.persistence.*;
import keenay.education.entity.status.AdvertisementResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "advertisement_response")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AdvertisementResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "seller_id", insertable = false, updatable = false)
    private Sellers seller;

    @Column(name = "seller_id")
    private Long sellerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advertisement_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Advertisement advertisement;

    @Column(name = "advertisement_id")
    private Long advertisementId;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AdvertisementResponseStatus status = AdvertisementResponseStatus.CREATED;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
