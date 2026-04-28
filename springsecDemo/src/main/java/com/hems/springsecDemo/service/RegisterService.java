package com.hems.springsecDemo.service;


import com.hems.springsecDemo.dao.RegisterRepo;
import com.hems.springsecDemo.model.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterService {
    @Autowired
    private RegisterRepo registerRepo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Register addUser(Register user){
        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        return registerRepo.save(user);
    }


    public List<Register> getUsers() {
       return registerRepo.findAll();
    }
}
