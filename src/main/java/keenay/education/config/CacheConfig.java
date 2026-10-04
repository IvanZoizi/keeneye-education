package keenay.education.config;

import keenay.education.cache.TarantoolCacheManager;
import keenay.education.mapper.customer.CustomerMapper;
import keenay.education.mapper.seller.SellerMapper;
import keenay.education.repository.tarantool.CustomersCacheRepository;
import keenay.education.repository.tarantool.SellersCacheRepository;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public TarantoolCacheManager cacheManager(
            CustomersCacheRepository customersCacheRepository,
            CustomerMapper customerMapper,
            SellersCacheRepository sellersCacheRepository,
            SellerMapper sellerMapper) {
        return new TarantoolCacheManager(customersCacheRepository, customerMapper, sellersCacheRepository, sellerMapper);
    }
}