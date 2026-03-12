package com.palmares.rrhh_backend.controller;

import com.palmares.rrhh_backend.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/api/test")
    public String test() {
        return "Backend funcionando correctamente";
    }

    @GetMapping("/api/test/db")
    public String testDb() {
        return testService.probarBase();
    }
}