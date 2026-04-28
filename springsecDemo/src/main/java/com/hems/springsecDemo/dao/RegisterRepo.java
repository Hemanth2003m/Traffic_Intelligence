package com.hems.springsecDemo.dao;

import com.hems.springsecDemo.model.Register;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterRepo extends JpaRepository<Register, Integer> {
        Register findByUsername(String username);
}
