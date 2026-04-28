package com.hems.springsecDemo.dao;

import com.hems.springsecDemo.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViolationRepository extends JpaRepository<Violation, Long> {
}