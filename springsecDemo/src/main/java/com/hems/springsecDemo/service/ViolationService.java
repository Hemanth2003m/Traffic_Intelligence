package com.hems.springsecDemo.service;

import com.hems.springsecDemo.dao.ViolationRepository;
import com.hems.springsecDemo.model.Violation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViolationService {

    @Autowired
    private ViolationRepository repo;

    @Autowired
    private EmailService emailService; // 🔥 Add this

    public Violation save(Violation v) {

        // 🔹 Ensure timestamp is set if not provided
        if (v.getTimestamp() == null) {
            v.setTimestamp(LocalDateTime.now());
        }

        // 🔹 Determine violation
        boolean isViolation = false;
        StringBuilder reason = new StringBuilder();

        if (v.getRiderCount() > 2) {
            isViolation = true;
            reason.append("Triple Riding ");
        }

        if (!v.isHelmet()) {
            isViolation = true;
            reason.append("No Helmet ");
        }

        // 🔹 Save first
        Violation saved = repo.save(v);

        // 🔹 Trigger email only if violation exists
        if (isViolation) {
            emailService.sendViolationAlert(saved, reason.toString());
        }

        return saved;
    }

    public List<Violation> getAll() {
        return repo.findAll();
    }
}