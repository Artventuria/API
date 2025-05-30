package com.artventuria.api.service.email;

import org.springframework.context.MessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Locale;

public class EmailTemplateBuilder {
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    // API URL for redirects
    private final String apiUrl;
    // Deeplink for mobile app
    private final String mobileDeeplink;

    public EmailTemplateBuilder(TemplateEngine templateEngine, MessageSource messageSource,
            String apiUrl, String mobileDeeplink) {
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.apiUrl = apiUrl;
        this.mobileDeeplink = mobileDeeplink;
    }

    public String buildPasswordResetHtml(String resetToken, String lang) {
        Context context = new Context(new Locale(lang));
        // Use the redirect endpoint to ensure link compatibility in emails
        // This will redirect to the mobile deeplink after token validation
        String resetLink = apiUrl + "/api/auth/reset-redirect?token=" + resetToken;
        // The redirect link points to the mobile deeplink (mobileDeeplink)
        context.setVariable("resetLink", resetLink);
        context.setVariable("subject", getMessage("email.reset.subject", lang));
        context.setVariable("greeting", getMessage("email.reset.greeting", lang));
        context.setVariable("message", getMessage("email.reset.message", lang));
        context.setVariable("button", getMessage("email.reset.button", lang));
        context.setVariable("expiry", getMessage("email.reset.expiry", lang));
        context.setVariable("ignore", getMessage("email.reset.ignore", lang));
        context.setVariable("signature", getMessage("email.reset.signature", lang));
        context.setVariable("alternativeLink", getMessage("email.reset.alternative", lang));
        context.setVariable("openApp", getMessage("email.reset.openapp", lang));
        return templateEngine.process("emails/password-reset", context);
    }

    public String buildPasswordResetText(String resetToken, String lang) {
        // Simple text version
        // Use the same redirect logic as for the HTML version
        String resetLink = apiUrl + "/api/auth/reset-redirect?token=" + resetToken;

        return String.format(
                "%s\n\n%s\n\n%s\n\n%s",
                getMessage("email.reset.subject", lang),
                getMessage("email.reset.message", lang),
                resetLink,
                getMessage("email.reset.expiry", lang));
    }

    public String buildFallbackText(String resetToken, String lang) {
        String message = getMessage("email.reset.alternative", lang);
        String linkText = getMessage("email.reset.alternative.link", lang);
        // Use the mobile deeplink directly as a fallback link to copy and paste
        String link = mobileDeeplink + "?token=" + resetToken;
        return message + "\n" + linkText + ": " + link;
    }

    public String getMessage(String code, String lang) {
        return messageSource.getMessage(code, null, new Locale(lang));
    }
}
