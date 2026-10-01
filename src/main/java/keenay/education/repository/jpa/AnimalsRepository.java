package keenay.education.repository.jpa;

import keenay.education.entity.Animals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnimalsRepository extends JpaRepository<Animals, Long> {
    @Query(value = "SELECT * FROM animals WHERE LOWER(name) = LOWER(?1)", nativeQuery = true)
    Optional<Animals> findByName(String name);

    @Modifying
    @Query("DELETE FROM Animals a WHERE a.name = :name")
    int deleteByName(@Param("name") String name);
}
