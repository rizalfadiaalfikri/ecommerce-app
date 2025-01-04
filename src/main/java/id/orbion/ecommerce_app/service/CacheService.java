package id.orbion.ecommerce_app.service;

import java.time.Duration;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

public interface CacheService {

    <T> Optional<T> get(String key, Class<T> clazz);

    <T> Optional<T> get(String key, TypeReference<T> typeReference);

    <T> void put(String key, T value);

    <T> void put(String key, T value, Duration ttl);

    void evict(String key);
}
