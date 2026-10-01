package keenay.education.repository.jpa;

import keenay.education.entity.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {
    Page<Messages> getMessagesByChat_IdOrderByCreatedAtDesc(Long chatId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE Messages m
           SET m.readedAt = :readAt
           WHERE m.chat.id = :chatId
             AND m.sender.id <> :userId
             AND m.readedAt IS NULL
           """)
    int markAsRead(@Param("chatId") Long chatId,
                   @Param("userId") Long userId,
                   @Param("readAt") LocalDateTime readAt);
}
