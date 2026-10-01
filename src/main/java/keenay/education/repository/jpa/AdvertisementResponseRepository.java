package keenay.education.repository.jpa;

import keenay.education.entity.AdvertisementResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementResponseRepository extends JpaRepository<AdvertisementResponse, Long> {

    Optional<AdvertisementResponse> findByIdAndSeller_Id(Long id, Long sellerId);
    Optional<AdvertisementResponse> findByIdAndAdvertisement_Id(Long id, Long advertisementId);
    List<AdvertisementResponse> findAllBySeller_Id(Long sellerId);
    List<AdvertisementResponse> findAllByAdvertisement_IdAndSeller_Id(Long advertisementId, Long sellerId);

    @Modifying
    @Query(value = "DELETE FROM advertisement_response WHERE id = :id AND seller_id = :seller_id", nativeQuery = true)
    void delete(@Param("id") Long id, @Param("seller_id") Long sellerId);

    @Query(value = "UPDATE advertisement_response SET status = :status WHERE id = :id AND seller_id = :seller_id RETURNING *",
    nativeQuery = true)
    List<AdvertisementResponse> updateStatus(@Param("id") Long id, @Param("seller_id") Long sellerId,
                                             @Param("status") String status);
}
