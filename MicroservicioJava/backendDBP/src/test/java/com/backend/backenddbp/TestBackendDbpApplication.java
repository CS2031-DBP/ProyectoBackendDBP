package com.backend.backenddbp;

import org.springframework.boot.SpringApplication;

public class TestBackendDbpApplication {

    public static void main(String[] args) {
        SpringApplication.from(BackendDbpApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
