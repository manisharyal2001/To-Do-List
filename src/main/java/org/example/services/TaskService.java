package org.example.services;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public class TaskService {
    private final MongoClient mongoClient;

    public TaskService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Future<String> addTask(JsonObject task) {
        return mongoClient.insert("tasks", task);
    }

    public Future<List<JsonObject>> getTasks(String userId) {
        JsonObject query = new JsonObject().put("userId", userId);
        return mongoClient.find("tasks", query);
    }

    public Future<Void> updateTaskStatus(String taskId, boolean completed) {
        JsonObject query = new JsonObject().put("_id", taskId);
        JsonObject update = new JsonObject().put("$set", new JsonObject().put("completed", completed));
        return mongoClient.updateCollection("tasks", query, update).mapEmpty();
    }

    public Future<Void> deleteTask(String taskId) {
        JsonObject query = new JsonObject().put("_id", taskId);
        return mongoClient.removeDocument("tasks", query).mapEmpty();
    }
}
