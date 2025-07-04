package org.example.services;

import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Command;
import io.vertx.core.Future;


public class RedisTokenService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RedisTokenService.class);
    private final RedisAPI redisAPI;

    public RedisTokenService(RedisAPI redisAPI) {
        this.redisAPI = redisAPI;
    }

    // Store a token with expiration in seconds
    public Future<Void> storeToken(String token, long expirySeconds) {
        return redisAPI.send(Command.SETEX, token, String.valueOf(expirySeconds), "1")
                .mapEmpty()
                .recover(err -> {
                    logger.warn("Redis unavailable, skipping token store: {}", err.getMessage());
                    return io.vertx.core.Future.succeededFuture();
                })
                .mapEmpty();
    }

    // Check if token is blacklisted (returns true if blacklisted)
    public Future<Boolean> isTokenBlacklisted(String token) {
        return redisAPI.send(Command.GET, token)
                .map(response -> response != null)
                .recover(err -> {
                    logger.warn("Redis unavailable, assuming token is not blacklisted: {}", err.getMessage());
                    return io.vertx.core.Future.<Boolean>succeededFuture(false);
                });
    }

    // Delete token (on logout or password reset)
    public Future<Void> deleteToken(String token) {
        return redisAPI.send(Command.DEL, token)
                .mapEmpty()
                .recover(err -> {
                    logger.warn("Redis unavailable, skipping token delete: {}", err.getMessage());
                    return io.vertx.core.Future.succeededFuture();
                })
                .mapEmpty();
    }
}
