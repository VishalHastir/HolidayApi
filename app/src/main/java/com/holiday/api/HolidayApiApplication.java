package com.holiday.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The type Holiday api application.
 */
@SpringBootApplication(scanBasePackages = "com.holiday.api")
public class HolidayApiApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(HolidayApiApplication.class, args);
    }

}
