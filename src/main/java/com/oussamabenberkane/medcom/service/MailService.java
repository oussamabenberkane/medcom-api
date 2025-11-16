package com.oussamabenberkane.medcom.service;

import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.domain.Product;
import com.oussamabenberkane.medcom.domain.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

/**
 * Service for sending emails asynchronously.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailService(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        sendEmailSync(to, subject, content, isMultipart, isHtml);
    }

    private void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        LOG.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content
        );

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            LOG.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            LOG.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        sendEmailFromTemplateSync(user, templateName, titleKey);
    }

    private void sendEmailFromTemplateSync(User user, String templateName, String titleKey) {
        if (user.getEmail() == null) {
            LOG.debug("Email doesn't exist for user '{}'", user.getLogin());
            return;
        }
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmailSync(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendActivationEmail(User user) {
        LOG.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/activationEmail", "email.activation.title");
    }

    @Async
    public void sendCreationEmail(User user) {
        LOG.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user) {
        LOG.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/passwordResetEmail", "email.reset.title");
    }

    /**
     * Send product availability alert email.
     *
     * @param user The user to send the email to
     * @param product The product with availability change
     * @param available The current availability status
     * @param pharmacy The pharmacy
     * @return The message ID from the mail server (if available)
     */
    @Async
    public String sendAlertEmail(User user, Product product, Boolean available, Pharmacy pharmacy) {
        if (user.getEmail() == null) {
            LOG.debug("Email doesn't exist for user '{}'", user.getLogin());
            return null;
        }

        LOG.debug("Sending availability alert email to '{}' for product '{}'", user.getEmail(), product.getName());

        Locale locale = Locale.forLanguageTag(user.getLangKey() != null ? user.getLangKey() : "en");
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        context.setVariable("productName", product.getName());
        context.setVariable("productCode", product.getCode());
        context.setVariable("officialUrl", product.getOfficialUrl());
        context.setVariable("available", available);
        context.setVariable("pharmacyName", pharmacy != null ? pharmacy.getName() : "Unknown Pharmacy");

        String content = templateEngine.process("mail/alertEmail", context);
        String subject = String.format("Product Availability Alert: %s", product.getName());

        try {
            sendEmailSync(user.getEmail(), subject, content, false, true);
            // Note: To get the actual message ID, we'd need to extract it from the sent message
            // For now, returning a simple confirmation
            return "sent";
        } catch (Exception e) {
            LOG.error("Failed to send alert email to {}: {}", user.getEmail(), e.getMessage());
            return null;
        }
    }

    /**
     * Send consolidated alert email with multiple products.
     *
     * @param user The user to send the email to
     * @param products List of product data maps containing name, code, available, officialUrl
     * @param pharmacy The pharmacy
     * @return The message ID from the mail server (if available)
     */
    @Async
    public String sendConsolidatedAlertEmail(User user, List<Map<String, Object>> products, Pharmacy pharmacy) {
        if (user.getEmail() == null) {
            LOG.debug("Email doesn't exist for user '{}'", user.getLogin());
            return null;
        }

        LOG.debug("Sending consolidated alert email to '{}' for {} products", user.getEmail(), products.size());

        Locale locale = Locale.forLanguageTag(user.getLangKey() != null ? user.getLangKey() : "en");
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        context.setVariable("products", products);
        context.setVariable("pharmacyName", pharmacy != null ? pharmacy.getName() : "Unknown Pharmacy");

        String content = templateEngine.process("mail/consolidatedAlertEmail", context);
        String subject = String.format("Watchlist Alert: %d product(s) availability changed", products.size());

        try {
            sendEmailSync(user.getEmail(), subject, content, false, true);
            return "sent";
        } catch (Exception e) {
            LOG.error("Failed to send consolidated alert email to {}: {}", user.getEmail(), e.getMessage());
            return null;
        }
    }
}
