package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.repository.TestRepository;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public String probarBase() {
        Integer resultado = testRepository.probarConexion();

        if (resultado != null && resultado == 1) {
            return "Conexión a SQL Server OK";
        }

        return "La base respondió, pero el resultado no fue el esperado";
    }
}