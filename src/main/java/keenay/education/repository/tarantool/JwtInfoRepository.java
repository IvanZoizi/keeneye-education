package keenay.education.repository.tarantool;

import keenay.education.entity.JwtInfo;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtInfoRepository extends KeyValueRepository<JwtInfo, String> {
    Optional<JwtInfo> findByUserId(Long userId);
}
