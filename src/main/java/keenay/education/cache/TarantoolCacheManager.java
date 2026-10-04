package keenay.education.cache;

import keenay.education.dto.users.CustomerDTO;
import keenay.education.dto.users.SellerDTO;
import keenay.education.entity.CustomersCache;
import keenay.education.entity.SellerCache;
import keenay.education.mapper.customer.CustomerMapper;
import keenay.education.mapper.seller.SellerMapper;
import keenay.education.repository.tarantool.CustomersCacheRepository;
import keenay.education.repository.tarantool.SellersCacheRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TarantoolCacheManager implements CacheManager {
    private final Map<String, Cache> caches;

    public TarantoolCacheManager(CustomersCacheRepository customersCacheRepository,
                                 CustomerMapper customerMapper,
                                 SellersCacheRepository sellersCacheRepository,
                                 SellerMapper sellerMapper) {
        this.caches = new ConcurrentHashMap<>();
        this.caches.put("customers", new TarantoolCache<CustomersCache, CustomerDTO>
                ("customers", customersCacheRepository, customerMapper, CustomersCache.class, CustomerDTO.class));
        this.caches.put("sellers", new TarantoolCache<SellerCache, SellerDTO>
                ("sellers", sellersCacheRepository, sellerMapper, SellerCache.class, SellerDTO.class));
    }

    @Override
    public @Nullable Cache getCache(String name) {
        return this.caches.get(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.caches.keySet().stream().toList();
    }
}
