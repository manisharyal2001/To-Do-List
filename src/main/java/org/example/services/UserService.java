package org.example.services;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.example.utils.JwtUtils;
import org.example.utils.PasswordGenerator;
import org.example.utils.EmailTemplates;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final MongoClient mongoClient;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;
    private final RedisTokenService redisTokenService;

    public UserService(MongoClient mongoClient, EmailService emailService,
                       JwtUtils jwtUtils, RedisTokenService redisTokenService) {
        this.mongoClient = mongoClient;
        this.emailService = emailService;
        this.jwtUtils = jwtUtils;
        this.redisTokenService = redisTokenService;
    }

    public void register(JsonObject userData, io.vertx.ext.web.RoutingContext ctx) {
        String email = userData.getString("email");
        String name = userData.getString("name");

        mongoClient.findOne("users", new JsonObject().put("email", email), null)
                .compose(existing -> {
                    if (existing != null) {
                        return Future.failedFuture("Email already registered.");
                    }

                    String password = PasswordGenerator.generateRandomPassword(10);
                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                    JsonObject newUser = new JsonObject()
                            .put("name", name)
                            .put("email", email)
                            .put("hashedPassword", hashedPassword);

                    return mongoClient.insert("users", newUser)
                            .compose(id -> {
                                String html = EmailTemplates.registrationEmail(name, password);
                                emailService.sendEmail(email, "Your Account Password", html);
                                return Future.succeededFuture(id);
                            });
                })
                .onSuccess(id -> ctx.response().setStatusCode(201).end("User registered successfully"))
                .onFailure(err -> ctx.response().setStatusCode(400).end(err.getMessage()));
    }

    public void login(String email, String password, io.vertx.ext.web.RoutingContext ctx) {
        mongoClient.findOne("users", new JsonObject().put("email", email), null)
                .compose(user -> {
                    if (user == null || !BCrypt.checkpw(password, user.getString("hashedPassword"))) {
                        return Future.failedFuture("Invalid credentials");
                    }

                    String userId = user.getString("_id");
                    String token = jwtUtils.generateToken(userId);

                    return redisTokenService.storeToken(token, 30 * 60) // 30 min expiry
                            .map(v -> token);
                })
                .onSuccess(token -> {
                    ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .end(new JsonObject().put("token", token).encode());
                })
                .onFailure(err -> ctx.response().setStatusCode(401).end(err.getMessage()));
    }

    public void logout(String token, io.vertx.ext.web.RoutingContext ctx) {
        redisTokenService.deleteToken(token)
                .onSuccess(v -> ctx.response().end("Logged out successfully"))
                .onFailure(err -> ctx.response().setStatusCode(500).end("Logout failed"));
    }

    public void requestPasswordReset(String email, io.vertx.ext.web.RoutingContext ctx) {
        mongoClient.findOne("users", new JsonObject().put("email", email), null)
                .compose(user -> {
                    if (user == null) {
                        return Future.failedFuture("No user found with this email");
                    }

                    String resetToken = UUID.randomUUID().toString();
                    JsonObject update = new JsonObject()
                            .put("$set", new JsonObject()
                                    .put("resetToken", resetToken)
                                    .put("resetTokenExpiry", System.currentTimeMillis() + 15 * 60 * 1000));

                    return mongoClient.updateCollection("users",
                                    new JsonObject().put("email", email), update)
                            .map(v -> resetToken);
                })
                .onSuccess(token -> {
                    String resetLink = "http://localhost:8888/reset-password?token=" + token;
                    String html = EmailTemplates.resetPasswordEmail(resetLink);
                    emailService.sendEmail(email, "Password Reset", html);
                    ctx.response().end("Reset link sent to your email");
                })
                .onFailure(err -> ctx.response().setStatusCode(400).end(err.getMessage()));
    }

    public void completePasswordReset(String token, String newPassword, io.vertx.ext.web.RoutingContext ctx) {
        mongoClient.findOne("users", new JsonObject().put("resetToken", token), null)
                .compose(user -> {
                    if (user == null || System.currentTimeMillis() > user.getLong("resetTokenExpiry", 0L)) {
                        return Future.failedFuture("Invalid or expired reset token");
                    }

                    String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                    JsonObject update = new JsonObject()
                            .put("$set", new JsonObject()
                                    .put("hashedPassword", hashed))
                            .put("$unset", new JsonObject()
                                    .put("resetToken", "")
                                    .put("resetTokenExpiry", ""));

                    return mongoClient.updateCollection("users", new JsonObject().put("resetToken", token), update);
                })
                .onSuccess(v -> ctx.response().end("Password has been reset"))
                .onFailure(err -> ctx.response().setStatusCode(400).end(err.getMessage()));
    }

    public Future<JsonObject> getUserById(String userId) {
        return mongoClient.findOne("users", new JsonObject().put("_id", userId), null);
    }
}
