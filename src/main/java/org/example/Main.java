package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import org.example.config.ConfigLoader;
import org.example.routes.AuthRoutes;
import org.example.routes.TaskRoutes;
import org.example.services.*;
import org.example.utils.JwtUtils;
import io.vertx.ext.web.handler.StaticHandler;
import java.net.BindException;

public class Main {
    public static void main(String[] args) {
        // Reduce SLF4J SimpleLogger noise
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        Vertx vertx = Vertx.vertx();
        // Ensure Vert.x closes on JVM shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(vertx::close));

        // Load config
        ConfigLoader.load(config -> {
            if (config.failed()) {
                System.err.println("Failed to load config: " + config.cause().getMessage());
                return;
            }

            JsonObject appConfig = config.result();

            // MongoDB setup - build config manually from properties keys
            String mongoUri = appConfig.getString("mongo.connection_string", "mongodb://localhost:27017");
            String mongoDb = appConfig.getString("mongo.db_name", "To_Do_List");
            JsonObject mongoConfig = new JsonObject()
                    .put("connection_string", mongoUri)
                    .put("db_name", mongoDb);
            MongoClient mongoClient = MongoClient.createShared(vertx, mongoConfig);

            // Redis setup
            String redisUri = appConfig.getString("redis.uri", "redis://localhost:6379");
            Redis redis = Redis.createClient(vertx, new RedisOptions().setConnectionString(redisUri));
            RedisAPI redisAPI = RedisAPI.api(redis);

            // Email setup
            JsonObject emailConfig = new JsonObject()
                    .put("host", appConfig.getString("email.host"))
                    .put("port", appConfig.getInteger("email.port", 587))
                    .put("username", appConfig.getString("email.username"))
                    .put("password", appConfig.getString("email.password"))
                    .put("from", appConfig.getString("email.from"));
            EmailService emailService = new EmailService(vertx, emailConfig);

            // JWT setup
            JwtUtils jwtUtils = new JwtUtils(appConfig.getString("jwtSecret"), appConfig.getLong("jwtExpiryMinutes", 30L));

            // Services
            RedisTokenService redisTokenService = new RedisTokenService(redisAPI);
            UserService userService = new UserService(mongoClient, emailService, jwtUtils, redisTokenService);
            TaskService taskService = new TaskService(mongoClient);

            // Routes & HTTP server
            Router router = Router.router(vertx);
            // Enable automatic request body parsing for JSON and form data
            router.route().handler(io.vertx.ext.web.handler.BodyHandler.create());

            // Auth routes
            AuthRoutes authRoutes = new AuthRoutes(userService);
            authRoutes.registerRoutes(router);

            // Task routes
            TaskRoutes taskRoutes = new TaskRoutes(taskService, redisAPI, jwtUtils);
            taskRoutes.registerRoutes(router);

            // Serve frontend static files from resources/web
            router.route("/*").handler(StaticHandler.create("web"));

            int httpPort = appConfig.getInteger("httpPort", 8888);
            vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(httpPort, ar -> {
                        if (ar.succeeded()) {
                            System.out.println("✅ To-Do List API is running. Open http://localhost:" + httpPort + "/index.html in your browser to view the app.");
                        } else if (ar.cause() instanceof BindException) {
                            System.err.println("Port " + httpPort + " is already in use. Stop the other instance or change the port in config.json.");
                        } else {
                            System.err.println("Failed to start HTTP server: " + ar.cause().getMessage());
                        }
                    });


        });
    }
}
