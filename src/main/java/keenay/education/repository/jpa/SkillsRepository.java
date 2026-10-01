package keenay.education.repository.jpa;

import keenay.education.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillsRepository extends JpaRepository<Skills, Long> {
    Optional<Skills> findAllByIdAndSeller_Id(Long id, Long sellerId);
    List<Skills> findAllBySeller_Id(Long sellerId);

    @Query(value = """
    DELETE FROM skills
    WHERE id = :id AND seller_id = :seller_id
    RETURNING *
    """, nativeQuery = true)
    List<Skills> deleteAndReturning(@Param("id") Long id, @Param("seller_id") Long sellerId);

    @Query(value = """
    UPDATE skills SET title = :title, description = :description WHERE id = :id AND seller_id = :seller_id
    RETURNING *
""", nativeQuery = true)
    List<Skills> update(@Param("id") Long id, @Param("seller_id") Long sellerId, @Param("title") String title,
                        @Param("description") String description);
}
