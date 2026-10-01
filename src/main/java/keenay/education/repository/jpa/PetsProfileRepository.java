package keenay.education.repository.jpa;

import keenay.education.entity.PetsProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetsProfileRepository extends JpaRepository<PetsProfile, Long> {
    @Query(value = """
        UPDATE pets_profile
        SET breed = :breed,
            features = :features,
            vaccinations = :vaccinations
        FROM pets
        WHERE pets.id = pets_profile.pet_id
          AND pets_profile.id = :id
          AND pets.customer_id = :customerId
        RETURNING *
        """, nativeQuery = true)
    List<PetsProfile> update(Long id, Long customerId,
                             String breed, String features, String vaccinations);
}
