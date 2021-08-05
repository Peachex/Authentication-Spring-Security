package com.epam.esm;

import com.epam.esm.constant.ProfileName;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootApp {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringBootApp.class);
        application.setAdditionalProfiles(ProfileName.PRODUCTION);
        application.run(args);
    }
}
