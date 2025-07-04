package org.example.routes;

import io.vertx.core.Future;
import io.vertx.core.Promise;

import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.RedisAPI;
import org.example.services.TaskService;
import org.example.utils.JwtUtils;


public class TaskRoutes {
    private final TaskService taskService;
    private final RedisAPI redisAPI;
    private final JwtUtils jwtUtils;

    public TaskRoutes(TaskService taskService, RedisAPI redisAPI, JwtUtils jwtUtils) {
        this.taskService = taskService;
        this.redisAPI = redisAPI;
        this.jwtUtils = jwtUtils;
    }

    public void registerRoutes(Router router) {
        router.post("/api/tasks").handler(this::addTask);
        router.get("/api/tasks").handler(this::getTasks);
        router.post("/api/tasks/:id/complete").handler(ctx -> toggleCompletion(ctx, true));
        router.post("/api/tasks/:id/incomplete").handler(ctx -> toggleCompletion(ctx, false));
        router.delete("/api/tasks/:id").handler(this::deleteTask);
    }

    private void addTask(RoutingContext ctx) {
        String token = ctx.request().getHeader("Authorization");
        validateToken(token).onSuccess(userId -> {
            JsonObject task = ctx.body().asJsonObject();
            task.put("userId", userId);

            taskService.addTask(task).onSuccess(id -> {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(new JsonObject().put("message", "Task added").put("id", id).encode());
            }).onFailure(err -> {
                ctx.response()
                        .setStatusCode(500)
                        .end(new JsonObject().put("error", "Failed to add task").encode());
            });
        }).onFailure(err -> {
            ctx.response()
                    .setStatusCode(401)
                    .end(new JsonObject().put("error", "Unauthorized").encode());
        });
    }

    private void getTasks(RoutingContext ctx) {
        String token = ctx.request().getHeader("Authorization");
        validateToken(token).onSuccess(userId -> {
            taskService.getTasks(userId).onSuccess(tasks -> {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(new JsonObject().put("tasks", tasks).encode());
            }).onFailure(err -> {
                ctx.response()
                        .setStatusCode(500)
                        .end(new JsonObject().put("error", "Failed to fetch tasks").encode());
            });
        }).onFailure(err -> {
            ctx.response()
                    .setStatusCode(401)
                    .end(new JsonObject().put("error", "Unauthorized").encode());
        });
    }

    private void toggleCompletion(RoutingContext ctx, boolean completed) {
        String token = ctx.request().getHeader("Authorization");
        validateToken(token).onSuccess(userId -> {
            String taskId = ctx.pathParam("id");
            taskService.updateTaskStatus(taskId, completed).onSuccess(v -> {
                ctx.response().end(new JsonObject().put("message", "Status updated").encode());
            }).onFailure(err -> ctx.response().setStatusCode(500).end(new JsonObject().put("error", "Failed to update").encode()));
        }).onFailure(err -> ctx.response().setStatusCode(401).end(new JsonObject().put("error", "Unauthorized").encode()));
    }

    private void deleteTask(RoutingContext ctx) {
        String token = ctx.request().getHeader("Authorization");
        validateToken(token).onSuccess(userId -> {
            String taskId = ctx.pathParam("id");
            taskService.deleteTask(taskId).onSuccess(v -> {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(new JsonObject().put("message", "Task deleted").encode());
            }).onFailure(err -> {
                ctx.response()
                        .setStatusCode(500)
                        .end(new JsonObject().put("error", "Failed to delete task").encode());
            });
        }).onFailure(err -> {
            ctx.response()
                    .setStatusCode(401)
                    .end(new JsonObject().put("error", "Unauthorized").encode());
        });
    }

    private Future<String> validateToken(String token) {
        Promise<String> promise = Promise.promise();
        // Remove "Bearer " prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isEmpty()) {
            promise.fail("Missing token");
            return promise.future();
        }

        String userId = jwtUtils.validateToken(token);
        if (userId == null) {
            promise.fail("Invalid token");
            return promise.future();
        }
        redisAPI.get(token).onSuccess(stored -> {
            if (stored != null) {
                promise.complete(userId);
            } else {
                promise.fail("Token mismatch");
            }
        }).onFailure(err -> {
            // If Redis is down, assume token is valid but log warning
            System.err.println("Redis unavailable, skipping token validation: " + err.getMessage());
            promise.complete(userId);
        });
        return promise.future();
    }
}
