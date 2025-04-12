package com.emaple.gestiondespatients;

import com.emaple.gestiondespatients.entities.Patient;
import com.emaple.gestiondespatients.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import java.util.Date;

@SpringBootApplication
public class GestiondespatientsApplication implements CommandLineRunner {
 @Autowired
    private PatientRepository patientRepository;
    public static void main(String[] args) {
        SpringApplication.run(GestiondespatientsApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
//        Patient patient = new Patient();
//        patient.setId(null);
//        patient.setNon("Mohammed");
//        patient.setDateNaissance(new Date());
//        patient.setMalade(false);
//        patient.setScore(23);
//
//
//        Patient patient3 = Patient.builder()
//                .non("Imane")
//                .dateNaissance(new Date())
//                .malade(true)
//                .score(56)
//                .build();



//        patientRepository.save( new Patient(null,"Mohammed",new Date(),false , 334));
//        patientRepository.save( new Patient(null,"Hanane",new Date(),false , 4321));
//        patientRepository.save( new Patient(null,"Imane",new Date(),false , 344));


    }

    @Bean
    CommandLineRunner commandLineRunner(JdbcUserDetailsManager jdbcUserDetailsManager) {
    PasswordEncoder passwordEncoder = passwordEncoder();
    return args -> {

        UserDetails u1 = jdbcUserDetailsManager.loadUserByUsername("user11");
        if (u1==null)
        jdbcUserDetailsManager.createUser(
                User.withUsername("user11").password(passwordEncoder.encode("1234")).roles("USER").build()

        );

        UserDetails u2 = jdbcUserDetailsManager.loadUserByUsername("user22");
        if (u2==null)
        jdbcUserDetailsManager.createUser(
                User.withUsername("user22").password(passwordEncoder.encode("1234")).roles("USER").build()

        );
        UserDetails u3 = jdbcUserDetailsManager.loadUserByUsername("admin2");
        if (u3==null)
        jdbcUserDetailsManager.createUser(
                User.withUsername("admin2").password(passwordEncoder.encode("1234")).roles("USER","ADMIN").build()

        );
    };

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
