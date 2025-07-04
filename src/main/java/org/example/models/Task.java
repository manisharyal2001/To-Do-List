package org.example.models;

import io.vertx.core.json.JsonObject;

public class Task {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String dueDate;
    private String priority; // low, medium, high
    private String status;   // completed, incomplete
    private String createdAt;
    private String updatedAt;

    public Task() {}

    public static Task fromJson(JsonObject json) {
        Task task = new Task();
        task.id = json.getString("_id");
        task.userId = json.getString("userId");
        task.title = json.getString("title");
        task.description = json.getString("description");
        task.dueDate = json.getString("dueDate");
        task.priority = json.getString("priority");
        task.status = json.getString("status");
        task.createdAt = json.getString("createdAt");
        task.updatedAt = json.getString("updatedAt");
        return task;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("userId", userId)
                .put("title", title)
                .put("description", description)
                .put("dueDate", dueDate)
                .put("priority", priority)
                .put("status", status)
                .put("createdAt", createdAt)
                .put("updatedAt", updatedAt);
    }

    // Getters and Setters...

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
