package keenay.education.repository.tarantool;

import keenay.education.entity.CustomersCache;
import keenay.education.entity.JwtInfo;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomersCacheRepository extends KeyValueRepository<CustomersCache, Long> {
    Optional<CustomersCache> findById(Long id);
}
