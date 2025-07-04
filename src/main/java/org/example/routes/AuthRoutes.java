package org.example.routes;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.example.services.UserService;

public class AuthRoutes {

    private final UserService userService;

    public AuthRoutes(UserService userService) {
        this.userService = userService;
    }

    public void registerRoutes(Router router) {
        router.post("/api/register").handler(this::handleRegister);
        router.post("/api/login").handler(this::handleLogin);
        router.post("/api/logout").handler(this::handleLogout);
    }

    private void handleRegister(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        userService.register(body, ctx);
    }

    private void handleLogin(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String email = body.getString("email");
        String password = body.getString("password");
        userService.login(email, password, ctx);
    }

    private void handleLogout(RoutingContext ctx) {
        String token = ctx.request().getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        userService.logout(token, ctx);
    }
}
