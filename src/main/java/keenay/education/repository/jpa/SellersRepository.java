package keenay.education.repository.jpa;

import keenay.education.entity.Sellers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellersRepository extends JpaRepository<Sellers, Long> {

    @Query(value = """
        UPDATE sellers
        SET name = :name, surname = :surname, address = :address, description = :description, inn = :inn
        WHERE id = :id
        RETURNING *
""", nativeQuery = true)
    List<Sellers> update(@Param("id") Long id, @Param("name") String name,
                         @Param("surname") String surname, @Param("address") String address,
                         @Param("description") String description, @Param("inn") String inn);
}
