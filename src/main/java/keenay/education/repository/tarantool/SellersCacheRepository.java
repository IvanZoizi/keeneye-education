package keenay.education.repository.tarantool;

import keenay.education.entity.SellerCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellersCacheRepository extends KeyValueRepository<SellerCache, Long> {
}
