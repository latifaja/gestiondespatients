package com.emaple.gestiondespatients.security.repositories;

import com.emaple.gestiondespatients.security.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
}