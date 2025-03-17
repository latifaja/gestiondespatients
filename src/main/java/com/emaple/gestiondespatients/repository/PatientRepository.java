package com.emaple.gestiondespatients.repository;

import com.emaple.gestiondespatients.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long > {

    Page<Patient> findByNomContains(String keyword, Pageable pageable);
}
