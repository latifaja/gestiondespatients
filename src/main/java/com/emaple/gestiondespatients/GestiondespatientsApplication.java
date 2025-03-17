package com.emaple.gestiondespatients;

import com.emaple.gestiondespatients.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestiondespatientsApplication implements CommandLineRunner {
 @Autowired
    private PatientRepository patientRepository;
    public static void main(String[] args) {
        SpringApplication.run(GestiondespatientsApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

    }
}
