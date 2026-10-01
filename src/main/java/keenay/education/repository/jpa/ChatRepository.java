package keenay.education.repository.jpa;

import keenay.education.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByCustomer_IdAndSeller_Id(Long customerId, Long sellerId);
    List<Chat> findAllByCustomer_IdOrSeller_Id(Long customerId, Long sellerId);

    @Query(value = "SELECT * FROM chat WHERE id = :id AND (customer_id = :user_id OR seller_id = :user_id)", nativeQuery = true)
    Optional<Chat> findMessages(@Param("id") Long chatId, @Param("user_id") Long userId);
}
