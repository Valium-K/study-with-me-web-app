package dev.valium.studywithmewebapp;

import dev.valium.studywithmewebapp.controller.validator.SignUpFormValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class StudywithmewebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudywithmewebappApplication.class, args);
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
