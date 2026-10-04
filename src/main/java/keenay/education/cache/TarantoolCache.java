package keenay.education.cache;

import keenay.education.mapper.TarantoolMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TarantoolCache <E, D> implements Cache {

    private final String name;
    private final KeyValueRepository<E, Long> repository;
    private final TarantoolMapper<E, D> mapper;
    private final Class<E> entityClass;
    private final Class<D> dtoClass;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return repository;
    }

    @Override
    public @Nullable ValueWrapper get(Object key) {
        if (key == null) {
            return null;
        }
        return repository.findById((Long) key)
                .map(mapper::getDtoCache)
                .map(SimpleValueWrapper::new)
                .orElse(null);
    }

    @Override
    public @Nullable <T> T get(Object key, @Nullable Class<T> type) {
        ValueWrapper wrapper = get(key);
        if (wrapper == null) {
            return null;
        }
        Object value = wrapper.get();
        if (type != null && !type.isInstance(value)) {
            throw new IllegalStateException(
                    "Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T) value;
    }

    @Override
    public @Nullable <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper wrapper = get(key);
        if (wrapper != null) {
            return (T) wrapper.get();
        }
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        if (dtoClass.isInstance(value)) {
            E entity = mapper.toEntity(dtoClass.cast(value));
            repository.save(entity);
            return;
        }
        throw new IllegalArgumentException("This type is not supported.");
    }

    @Override
    public void evict(Object key) {
        repository.deleteById((Long) key);
    }

    @Override
    public void clear() {
        repository.deleteAll();
    }
}
