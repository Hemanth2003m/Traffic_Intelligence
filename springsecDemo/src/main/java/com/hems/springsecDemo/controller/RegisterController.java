package com.hems.springsecDemo.controller;


    import com.hems.springsecDemo.model.Register;
    import com.hems.springsecDemo.service.JwtService;
    import com.hems.springsecDemo.service.RegisterService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;
    @CrossOrigin(origins = "http://localhost:5173")
    @RestController
    public class RegisterController {
        @Autowired
        private RegisterService registerService;
        @Autowired
        private JwtService jwtService;
        @Autowired
        AuthenticationManager authenticationManager;
        @PostMapping("/register")
        public ResponseEntity<String> addUser(@RequestBody Register registerId){
            Register savedUser = registerService.addUser(registerId);

            if (savedUser != null && savedUser.getRegisterId() != 0) {
                return ResponseEntity.ok("User saved successfully: " + savedUser.getUsername());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not saved!");
            }
        }
        @GetMapping("/registers")
        public List<Register> addUser(){
            return registerService.getUsers();

        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Register user){
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            if(authentication.isAuthenticated()){
                String token = jwtService.generateToken(user.getUsername());
                return ResponseEntity.ok(Map.of("token", token));  // ✅ frontend expects JSON
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        }




    }
