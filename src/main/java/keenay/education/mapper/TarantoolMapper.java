package keenay.education.mapper;

public interface TarantoolMapper<E, D> {
    D getDtoCache(E entity);
    E toEntity(D dto);
}