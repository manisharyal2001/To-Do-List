package org.example.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ConfigLoader {

    public static void load(Handler<AsyncResult<JsonObject>> handler) {
        Vertx vertx = Vertx.vertx();

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file").setFormat("properties")
                .setConfig(new JsonObject().put("path", "src/main/resources/application.properties"));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(handler);
    }

    // Optional async Future-based config loader
    public static Future<JsonObject> loadConfig(Vertx vertx) {
        Promise<JsonObject> promise = Promise.promise();

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file").setFormat("properties")
                .setConfig(new JsonObject().put("path", "src/main/resources/application.properties"));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(promise);

        return promise.future();
    }
}
