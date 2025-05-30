package com.artventuria.api.service.email.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artventuria.api.service.email.EmailService;
import com.artventuria.api.service.email.EmailTemplateBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.regions.Region;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {
        private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

        private final SesClient sesClient;
        private final String fromEmail; // Format will be: "Artventuria <email@domain.com>"
        private final EmailTemplateBuilder emailTemplateBuilder;
        private final MessageSource messageSource;

        public EmailServiceImpl(
                        @Value("${aws.ses.region:eu-west-3}") String awsRegion,
                        @Value("${aws.ses.from-email}") String fromEmail,
                        @Value("${api.url}") String apiUrl,
                        @Value("${mobile.deeplink.reset-password}") String mobileDeeplink,
                        MessageSource messageSource,
                        TemplateEngine templateEngine) {
                this.sesClient = SesClient.builder()
                                .region(Region.of(awsRegion))
                                .build();
                this.fromEmail = fromEmail;
                this.messageSource = messageSource;
                this.emailTemplateBuilder = new EmailTemplateBuilder(templateEngine, messageSource, apiUrl,
                                mobileDeeplink);
        }

        @Override
        public void sendPasswordResetEmail(String to, String resetToken) throws Exception {
                logger.debug("Attempting to send password reset email to: {}", to);
                Locale locale = LocaleContextHolder.getLocale();
                String lang = locale.getLanguage().equals("fr") ? "fr" : "en";

                try {
                        String htmlBody = emailTemplateBuilder.buildPasswordResetHtml(resetToken, lang);
                        String textBody = emailTemplateBuilder.buildPasswordResetText(resetToken, lang);
                        String subject = emailTemplateBuilder.getMessage("email.reset.subject", lang);

                        SendEmailRequest request = SendEmailRequest.builder()
                                        .destination(Destination.builder().toAddresses(to).build())
                                        .message(Message.builder()
                                                        .subject(Content.builder().data(subject).charset("UTF-8")
                                                                        .build())
                                                        .body(Body.builder()
                                                                        .html(Content.builder().data(htmlBody)
                                                                                        .charset("UTF-8").build())
                                                                        .text(Content.builder().data(textBody)
                                                                                        .charset("UTF-8").build())
                                                                        .build())
                                                        .build())
                                        .source("Artventuria <" + fromEmail + ">")
                                        .build();

                        sesClient.sendEmail(request);
                        logger.info("Successfully sent password reset email to: {}", to);
                } catch (SesException e) {
                        String errorMessage = lang.equals("fr")
                                        ? "Erreur lors de l'envoi de l'email de réinitialisation : "
                                        : "Error sending password reset email: ";
                        logger.error("Failed to send email to {} - Reason: {}", to, e.getMessage());
                        throw new Exception(errorMessage + e.getMessage(), e);
                }
        }
}
