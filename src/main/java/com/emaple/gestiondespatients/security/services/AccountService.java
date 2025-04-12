package com.emaple.gestiondespatients.security.services;

import com.emaple.gestiondespatients.security.entities.AppRole;
import com.emaple.gestiondespatients.security.entities.AppUser;

public interface AccountService {
    AppUser addNewUser(String username, String password, String confirmPassword, String email);
    AppRole addNewRole(String role);
    void addRoleToUser(String username, String role);
    void removeRoleFromUser(String username, String role);
    AppUser loadUserByUsername(String username);
}