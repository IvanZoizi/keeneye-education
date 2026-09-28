package keenay.education.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "pets_profile")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PetsProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "pet_id")
    private Pets pet;

    @Column(name = "breed", nullable = false)
    private String breed;

    @Column(name = "features", nullable = false)
    private String features;

    @Column(name = "vaccinations", nullable = false)
    private String vaccinations;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
