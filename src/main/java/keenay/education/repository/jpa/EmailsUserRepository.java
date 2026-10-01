package keenay.education.repository.jpa;

import keenay.education.entity.EmailsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EmailsUserRepository extends JpaRepository<EmailsUser, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE EmailsUser SET attempt = attempt - 1, exceptionMessage = :exceptionMessage WHERE id = :id")
    void incrementCountRetry(@Param("id") Long id, @Param("exceptionMessage") String exceptionMessage);
}
