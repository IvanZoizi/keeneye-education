package keenay.education.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.impl.CustomUserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class UserDetailCache {
    private final CustomUserServiceImpl customUserService;
    private final Cache<String, CustomUserDetail> cache;

    public UserDetailCache(
            CustomUserServiceImpl customUserService,
            @Value("${caffeine.expire-minutes}") int duration,
            @Value("${caffeine.size}") long size
    ) {
        this.customUserService = customUserService;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(duration))
                .maximumSize(size)
                .build();
    }

    public CustomUserDetail getCustomUserDetail(String email) {
        return cache.get(email, customUserService::getUserByEmail);
    }

    public void evict(String email) {
        cache.invalidate(email);
    }

    public void evictAll() {
        cache.invalidateAll();
    }
}
