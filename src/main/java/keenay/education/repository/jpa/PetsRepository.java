package keenay.education.repository.jpa;

import keenay.education.entity.Pets;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetsRepository extends JpaRepository<Pets, Long> {
    @EntityGraph(attributePaths = {"petsProfile", "animal"})
    List<Pets> findByCustomer_Id(Long id);

    @EntityGraph(attributePaths = {"petsProfile", "animal"})
    List<Pets> findAll();

    @EntityGraph(attributePaths = {"petsProfile", "animal"})
    Optional<Pets> findById(Long id);

    @EntityGraph(attributePaths = {"petsProfile", "animal"})
    @Query("SELECT p FROM Pets p WHERE p.id = :id AND p.customer.user.id = :userId")
    Optional<Pets> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query(value = """
    DELETE FROM pets
    WHERE id = :id
      AND customer_id = (
        SELECT c.id FROM customers c WHERE c.user_id = :userId
      )
    RETURNING *
    """, nativeQuery = true)
    Optional<Pets> deleteByIdAndCustomer(@Param("id") Long id, @Param("userId") Long userId);
}
