package com.hems.springsecDemo.service;

import com.hems.springsecDemo.model.Violation;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendViolationAlert(Violation v, String reason) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            // true = multipart (required for attachments)
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("hemanth77765@gmail.com");
            helper.setSubject("🚨 Traffic Violation Detected");

            helper.setText(
                    "Violation: " + reason + "\n" +
                            "Vehicle Type: " + v.getVehicleType() + "\n" +
                            "Rider Count: " + v.getRiderCount() + "\n" +
                            "Helmet: " + v.isHelmet() + "\n" +
                            "Number Plate: " + v.getNumberPlate() + "\n" +
                            "Time: " + v.getTimestamp()
            );

            // 🔹 Attach Image
            if (v.getImageUrl() != null) {
                File file = new File(v.getImageUrl());

                if (file.exists()) {
                    helper.addAttachment("violation.jpg", file);
                } else {
                    System.out.println("❌ Image not found: " + v.getImageUrl());
                }
            }

            mailSender.send(message);

            System.out.println("✅ Email sent with image");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}