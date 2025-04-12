package com.emaple.gestiondespatients.security.repositories;

import com.emaple.gestiondespatients.security.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, String> {
}
