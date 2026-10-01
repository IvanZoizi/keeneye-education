package keenay.education.repository.jpa;

import keenay.education.entity.TicketReplies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepliesRepository extends JpaRepository<TicketReplies, Long> {
}
