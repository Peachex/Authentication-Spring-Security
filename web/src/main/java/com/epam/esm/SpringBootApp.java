package com.epam.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The type Spring boot app.
 */
@SpringBootApplication
public class SpringBootApp {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringBootApp.class);
        application.setAdditionalProfiles("prod");
        application.run(args); // TODO: 6/29/2021 set active profile
        // TODO: 6/29/2021 Rename classes: EntityFieldsName, ErrorAttribute
    }
}
