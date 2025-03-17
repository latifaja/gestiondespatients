package com.emaple.gestiondespatients.repository;

import com.emaple.gestiondespatients.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long > {
}
