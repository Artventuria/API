package com.artventuria.api.service.email;

import org.springframework.context.MessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Locale;

public class EmailTemplateBuilder {
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final String frontendUrl;

    public EmailTemplateBuilder(TemplateEngine templateEngine, MessageSource messageSource, String frontendUrl) {
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.frontendUrl = frontendUrl;
    }

    public String buildPasswordResetHtml(String resetToken, String lang) {
        Context context = new Context(new Locale(lang));
        context.setVariable("resetLink", frontendUrl + "/reset-password?token=" + resetToken);
        context.setVariable("subject", getMessage("email.reset.subject", lang));
        context.setVariable("greeting", getMessage("email.reset.greeting", lang));
        context.setVariable("message", getMessage("email.reset.message", lang));
        context.setVariable("button", getMessage("email.reset.button", lang));
        context.setVariable("expiry", getMessage("email.reset.expiry", lang));
        context.setVariable("ignore", getMessage("email.reset.ignore", lang));
        context.setVariable("signature", getMessage("email.reset.signature", lang));
        return templateEngine.process("emails/password-reset", context);
    }

    public String buildPasswordResetText(String resetToken, String lang) {
        // Text version simplified
        return String.format(
                "%s\n\n%s\n\n%s\n\n%s",
                getMessage("email.reset.subject", lang),
                getMessage("email.reset.message", lang),
                frontendUrl + "/reset-password?token=" + resetToken,
                getMessage("email.reset.expiry", lang));
    }

    public String getMessage(String code, String lang) {
        return messageSource.getMessage(code, null, new Locale(lang));
    }
}
