package org.example.utils;

public class EmailTemplates {

    public static String registrationEmail(String name, String password) {
        return "<h2>Welcome, " + name + "!</h2>" +
                "<p>Your account has been created. Use the password below to log in:</p>" +
                "<strong>" + password + "</strong><br/><br/>" +
                "<p>Change your password after login for better security.</p>";
    }

    public static String resetPasswordEmail(String resetLink) {
        return "<h3>Password Reset Request</h3>" +
                "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + resetLink + "\">Reset Password</a><br/><br/>" +
                "<p>This link will expire in 15 minutes.</p>";
    }

    public static String taskReminderEmail(String taskTitle, String dueDate) {
        return "<h3>⏰ Task Reminder</h3>" +
                "<p>Your task <strong>" + taskTitle + "</strong> is due on <strong>" + dueDate + "</strong>.</p>";
    }
}
