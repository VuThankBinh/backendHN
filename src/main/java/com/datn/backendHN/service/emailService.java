package com.datn.backendHN.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class emailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otp) {
        try {
            log.info("Bắt đầu quá trình gửi email đến: {}", email);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("aigoaitutor@gmail.com", "Medical Booking Support");
            helper.setTo(email);
            helper.setSubject("[Medical Booking] Mã xác thực OTP của bạn");

            message.addHeader("List-Unsubscribe", "<mailto:aigoaitutor@gmail.com>");
            message.addHeader("Precedence", "bulk");
            message.addHeader("X-Auto-Response-Suppress", "OOF, AutoReply");

            try {
                String template = new String(getClass().getResourceAsStream("/templates/email/otp-template.html").readAllBytes());
                String content = String.format(template, otp);
                helper.setText(content, true);
                log.info("Set email content successfully");
            } catch (Exception e) {
                log.error("Error reading email template: {}", e.getMessage());
                throw new RuntimeException("Error reading email template");
            }

            log.info("Starting email sending...");
            mailSender.send(message);
            log.info("Email sent successfully to: {}", email);

        } catch (Exception e) {
            log.error("Error details when sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Cannot send email: " + e.getMessage());
        }
    }
}