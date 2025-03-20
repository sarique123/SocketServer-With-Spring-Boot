package org.example.ubersocketserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "org.example.uberprojectentityservice.models")
public class UberSocketServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UberSocketServerApplication.class, args);
    }

}
