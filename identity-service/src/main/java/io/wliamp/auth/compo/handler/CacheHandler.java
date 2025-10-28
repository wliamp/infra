package io.wliamp.auth.compo.handler;

import java.time.Duration;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CacheHandler {
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    /**
     * Put value with TTL
     */
    public <T> Mono<Boolean> put(String key, T value, Duration ttl) {
        return redisTemplate.opsForValue().set(key, value, ttl).onErrorResume(e -> Mono.just(false));
    }

    /**
     * Put value without TTL (persist until evicted)
     */
    public <T> Mono<Boolean> put(String key, T value) {
        return redisTemplate.opsForValue().set(key, value).onErrorResume(e -> Mono.just(false));
    }

    /**
     * Get cached value
     */
    public <T> Mono<T> get(String key, Class<T> type) {
        return redisTemplate.opsForValue().get(key).map(type::cast);
    }

    /**
     * Delete key
     */
    public Mono<Boolean> evict(String key) {
        return redisTemplate.delete(key).map(count -> count > 0).onErrorReturn(false);
    }

    /**
     * Check if key exists
     */
    public Mono<Boolean> exists(String key) {
        return redisTemplate.hasKey(key).onErrorReturn(false);
    }

    /**
     * Set TTL for a key
     */
    public Mono<Boolean> expire(String key, Duration ttl) {
        return redisTemplate.expire(key, ttl).onErrorReturn(false);
    }

    /**
     * Get TTL of a key (in seconds)
     */
    public Mono<Duration> ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * Increment numeric value
     */
    public Mono<Long> increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Mono<Long> increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * Decrement numeric value
     */
    public Mono<Long> decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    public Mono<Long> decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * Keys matching pattern (expensive in production, use carefully!)
     */
    public Flux<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * Bulk delete keys
     */
    public Mono<Long> evictAll(Collection<String> keys) {
        return redisTemplate.delete(Flux.fromIterable(keys));
    }

    // =====================================================
    // Token-specific helpers (Optional)
    // =====================================================

    /**
     * Blacklist token (add to Redis with TTL)
     */
    public Mono<Boolean> blacklistToken(String token, Duration ttl) {
        String key = "blacklist:" + token;
        return put(key, true, ttl);
    }

    /**
     * Check if token is blacklisted
     */
    public Mono<Boolean> isTokenBlacklisted(String token) {
        String key = "blacklist:" + token;
        return exists(key);
    }
}
