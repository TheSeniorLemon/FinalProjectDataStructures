package co.edu.uniquindio.techpark.service;

import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Gmail setup:
 *   1. Enable 2-Step Verification on your Google account.
 *   2. Go to Google Account > Security > App Passwords.
 *   3. Generate an app password for "Mail".
 *   4. Call EmailService.configure("your@gmail.com", "xxxx xxxx xxxx xxxx") once at startup.
 */
public class EmailService {

    private static final EmailService INSTANCE = new EmailService();

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    private String senderEmail = "";
    private String appPassword = "";
    private boolean configured = false;

    private EmailService() {}

    public static EmailService getInstance() { return INSTANCE; }

    // ----------------------------------------------------------------
    // configuration
    // ----------------------------------------------------------------
    public void configure(String email, String appPassword) {
        this.senderEmail = email;
        this.appPassword = appPassword;
        this.configured = !email.isBlank() && !appPassword.isBlank();
    }

    public boolean isConfigured() { return configured; }

    // ----------------------------------------------------------------
    // code generation
    // ----------------------------------------------------------------
    public String generateCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    // ----------------------------------------------------------------
    // send verification code
    // ----------------------------------------------------------------
    public boolean sendVerificationCode(String toEmail, String code) {
        String subject = "Tech-Park UQ - Password Reset Code";
        String body    =
                "<div style='font-family:Segoe UI,sans-serif;max-width:480px;margin:auto;'>" +
                        "<h2 style='color:#3882FF;'>Tech-Park UQ</h2>" +
                        "<p>You requested a password reset. Use the code below:</p>" +
                        "<div style='font-size:36px;font-weight:bold;letter-spacing:10px;" +
                        "color:#3882FF;padding:20px 0;'>" + code + "</div>" +
                        "<p style='color:#888;font-size:12px;'>This code expires in 10 minutes." +
                        " If you did not request this, ignore this email.</p>" +
                        "</div>";
        return send(toEmail, subject, body);
    }

    // ----------------------------------------------------------------
    // send welcome email
    // ----------------------------------------------------------------
    public boolean sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to Tech-Park UQ!";
        String body    =
                "<div style='font-family:Segoe UI,sans-serif;max-width:480px;margin:auto;'>" +
                        "<h2 style='color:#3882FF;'>Welcome, " + name + "!</h2>" +
                        "<p>Your account has been created successfully.</p>" +
                        "<p>Log in to explore attractions, plan your route and manage your visit.</p>" +
                        "<p style='color:#888;font-size:12px;'>Tech-Park UQ &mdash; " +
                        "Universidad del Quindio</p>" +
                        "</div>";
        return send(toEmail, subject, body);
    }

    // ----------------------------------------------------------------
    // core send
    // ----------------------------------------------------------------
    private boolean send(String toEmail, String subject, String htmlBody) {
        if (!configured) {
            System.err.println("[EmailService] Not configured. Call configure() first.");
            return false;
        }
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, appPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "Tech-Park UQ"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("[EmailService] Email sent to " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("[EmailService] Failed to send email: " + e.getMessage());
            return false;
        }
    }
}