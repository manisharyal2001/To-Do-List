package org.example.models;

import io.vertx.core.json.JsonObject;

public class User {
    private String id;
    private String name;
    private String email;
    private String hashedPassword;

    public User() {}

    public User(String id, String name, String email, String hashedPassword) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    public static User fromJson(JsonObject json) {
        return new User(
                json.getString("_id"),
                json.getString("name"),
                json.getString("email"),
                json.getString("hashedPassword")
        );
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("name", name)
                .put("email", email)
                .put("hashedPassword", hashedPassword);
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
