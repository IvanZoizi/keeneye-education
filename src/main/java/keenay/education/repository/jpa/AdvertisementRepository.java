package keenay.education.repository.jpa;

import keenay.education.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    Optional<Advertisement> findByIdAndCustomer_Id(Long id, Long id1);

    List<Advertisement> findAllByCustomer_Id(Long id);

    @Modifying
    @Query(value = "DELETE FROM advertisement WHERE id = :id AND client_id = :customer_id", nativeQuery = true)
    void delete(@Param("id") Long id, @Param("customer_id") Long customerId);

    @Query(value = "UPDATE advertisement SET status = :status WHERE id = :id AND client_id = :customer_id RETURNING *",
    nativeQuery = true)
    List<Advertisement> updateStatus(@Param("id") Long id, @Param("customer_id") Long customerId,
                                     @Param("status") String status);

    @Query("""
        SELECT a FROM Advertisement a
        JOIN a.pet p
        JOIN p.animal an
        WHERE an.id IN (
            SELECT s.animal.id FROM Skills s WHERE s.seller.id = :sellerId AND a.status = CREATED
        )
        """)
    List<Advertisement> findAdvertisementBySkills(@Param("sellerId") Long sellerId);
}
