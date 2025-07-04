package org.example.services;

import io.vertx.core.Vertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {
    private final Vertx vertx;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String from;
    private final MailClient mailClient;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.host = config.getString("host");
        this.port = config.getInteger("port");
        this.username = config.getString("username");
        this.password = config.getString("password");
        this.from = config.getString("from");

        MailConfig mailConfig = new MailConfig()
                .setHostname(host)
                .setPort(port)
                .setUsername(username)
                .setPassword(password)
                .setStarttls(StartTLSOptions.REQUIRED);

        this.mailClient = MailClient.createShared(this.vertx, mailConfig);
    }

    public Future<Void> sendEmail(String to, String subject, String htmlContent) {
        MailMessage message = new MailMessage()
                .setFrom(from)
                .setTo(to)
                .setSubject(subject)
                .setHtml(htmlContent);

        return mailClient.sendMail(message)
                .onSuccess(r -> logger.info("📧 Email sent to {}", to))
                .onFailure(err -> logger.error("❌ Failed to send email: {}", err.getMessage()))
                .mapEmpty();
    }
}
