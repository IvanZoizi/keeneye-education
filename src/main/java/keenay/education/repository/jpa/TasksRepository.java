package keenay.education.repository.jpa;

import keenay.education.entity.Tasks;
import keenay.education.entity.status.TasksStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    @EntityGraph(attributePaths = {"advertisement"})
    Optional<Tasks> findByIdAndCustomer_Id(Long id, Long customerId);

    @EntityGraph(attributePaths = {"advertisement"})
    List<Tasks> findAllByCustomer_Id(Long customerId);

    @EntityGraph(attributePaths = {"advertisement"})
    List<Tasks> findAllByCustomer_IdAndStatus(Long customerId, TasksStatus status);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tasks WHERE id = :id AND client_id = :customer_id RETURNING *", nativeQuery = true)
    List<Tasks> delete(@Param("id") Long id, @Param("customer_id") Long customerId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tasks SET title = :title, description = :description " +
            "WHERE id = :id AND client_id = :customer_id RETURNING *", nativeQuery = true)
    List<Tasks> updateTask(@Param("id") Long id,
                           @Param("customer_id") Long customerId,
                           @Param("title") String title,
                           @Param("description") String description);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tasks SET photo = :photo " +
            "WHERE id = :id AND client_id = :customer_id RETURNING *", nativeQuery = true)
    List<Tasks> updateTaskPhoto(@Param("id") Long id,
                                @Param("customer_id") Long customerId,
                                @Param("photo") String photo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tasks SET status = :status " +
            "WHERE id = :id AND client_id = :customer_id RETURNING *", nativeQuery = true)
    List<Tasks> updateTaskStatus(@Param("id") Long id,
                                 @Param("customer_id") Long customerId,
                                 @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tasks SET advertisement_id = :advertisement_id " +
            "WHERE id = :id AND client_id = :customer_id RETURNING *", nativeQuery = true)
    List<Tasks> deleteAdvertisement(@Param("id") Long id,
                                @Param("customer_id") Long customerId,
                                @Param("advertisement_id") Long advertisementId);
}