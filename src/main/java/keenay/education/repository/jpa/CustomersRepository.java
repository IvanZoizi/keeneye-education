package keenay.education.repository.jpa;

import keenay.education.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Long> {

    @Query(value = """
        UPDATE customers SET name = :name, surname = :surname
        WHERE id = :id
        RETURNING *
""", nativeQuery = true)
    List<Customers> update(@Param("id") Long id, @Param("name") String name, @Param("surname") String surname);
}
