

# Gestion des Patients App

**gestiondespatients** is a Spring Boot application designed to manage patient records. It uses Spring MVC for handling web requests, Spring Data JPA for database operations, Thymeleaf for templating, and Spring Security for authentication and authorization. The application starts with an H2 in-memory database for testing and is planned to migrate to MySQL with MariaDB dialect as features expand.

## Table of Contents
- [Technologies](#technologies)
- [Project Initialization](#project-initialization)
- [Entity](#entity)
- [Repository](#repository)
- [Controller](#controller)
- [Thymeleaf Templates](#thymeleaf-templates)
- [Validation](#validation)
- [Security](#security)
- [Custom User Management](#custom-user-management)
- [Setup and Running](#setup-and-running)

## Technologies
- **Spring Boot**: Framework for building the application.
- **Spring MVC**: For handling HTTP requests and responses.
- **Spring Data JPA**: For ORM and database operations.
- **H2 Database**: In-memory database for initial testing.
- **MySQL/MariaDB**: Planned database for production (future migration).
- **Thymeleaf**: Template engine for rendering dynamic HTML pages.
- **Lombok**: To reduce boilerplate code (getters, setters, constructors).
- **Spring Security**: For authentication and role-based authorization.
- **Spring Boot Starter Validation**: For form validation.
- **Maven**: Dependency management and build tool.

## Project Initialization
The project is initialized with the following dependencies using Spring Initializr or a similar setup:
- **H2 Database**: For in-memory database testing.
- **Spring Web**: For building web applications with Spring MVC.
- **Lombok**: For generating boilerplate code.
- **Spring Data JPA**: For database access and ORM.

To set up the project:
1. Create a new Spring Boot project.
2. Add the above dependencies in `pom.xml`.
3. Configure the H2 database in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.h2.console.enabled=true
   ```

The application will later migrate to MySQL/MariaDB by updating the `application.properties` with appropriate configurations.

## Entity
The core entity is the `Patient` class, which represents a patient in the database. It uses JPA and Lombok annotations for ORM and code simplification.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Size(min = 4, max = 10)
    private String nom;
    private Date dateNaissance;
    private boolean malade;
    @DecimalMin("100")
    private int score;
}
```

- **Annotations**:
  - `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`: Lombok annotations for getters, setters, constructors, and builder pattern.
  - `@Entity`: Marks the class as a JPA entity.
  - `@Id` and `@GeneratedValue`: Define the primary key with auto-increment.
  - Validation annotations (`@NotEmpty`, `@Size`, `@DecimalMin`) ensure data integrity.

## Repository
The `PatientRepository` interface extends `JpaRepository` to provide CRUD operations and custom queries for the `Patient` entity.

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByNomContains(String nom, Pageable pageable);
    
    @Query("select p from Patient p where p.nom like :x")
    Page<Patient> search(@Param("x") String nom, Pageable pageable);
}
```

- **Methods**:
  - `findByNomContains`: Spring Data JPA's derived query to search patients by name.
  - `search`: Custom JPQL query for name-based search with pagination support.
- **Pagination**: Both methods return a `Page` object to handle paginated results.

## Controller
The `PatientController` handles HTTP requests and renders Thymeleaf templates. It supports listing, deleting, adding, and editing patients.

```java
@Controller
public class PatientController {
    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/index")
    public String index(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "5") int size,
                       @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        Page<Patient> patients = patientRepository.findByNomContains(keyword, PageRequest.of(page, size));
        model.addAttribute("patients", patients.getContent());
        model.addAttribute("pages", new int[patients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "patients";
    }

    @GetMapping("/delete")
    public String delete(Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String keyword) {
        patientRepository.deleteById(id);
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }

    @PostMapping("/editPatient")
    public String editPatient(Model model,
                             @Valid Patient patient,
                             BindingResult bindingResult,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "") String keyword) {
        if (bindingResult.hasErrors()) return "form-patients";
        patientRepository.save(patient);
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }
}
```

- **Key Features**:
  - **Listing**: Displays patients with pagination and search by name.
  - **Deletion**: Removes a patient by ID and redirects to the index page.
  - **Editing/Adding**: Validates and saves patient data, redirecting on success or showing errors.
- **Annotations**:
  - `@RequestParam`: Binds query parameters (page, size, keyword).
  - `@Valid` and `BindingResult`: Enable form validation.

## Thymeleaf Templates
Thymeleaf is used to create dynamic HTML pages. A template (`template1.html`) defines a reusable layout with a navigation bar.

```html
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://ultraq.net.nz/thymeleaf/layout">
<head>
    <title>Patient Management</title>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <!-- Navigation links -->
    </nav>
    <section layout:fragment="content1"></section>
</body>
</html>
```

Other pages (e.g., `patients.html`, `form-patients.html`) extend the template:

```html
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://ultraq.net.nz/thymeleaf/layout"
      layout:decorate="template1">
<body>
    <div layout:fragment="content1">
        <!-- Page-specific content -->
    </div>
</body>
</html>
```

- **Key Features**:
  - Reusable navigation bar.
  - Dynamic content injection using `layout:fragment`.

## Validation
Form validation is implemented using Spring Boot Starter Validation. Constraints are defined on the `Patient` entity (e.g., `@NotEmpty`, `@Size`, `@DecimalMin`). The controller uses `@Valid` and `BindingResult` to enforce these rules:

```java
@PostMapping("/editPatient")
public String editPatient(Model model,
                         @Valid Patient patient,
                         BindingResult bindingResult,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "") String keyword) {
    if (bindingResult.hasErrors()) return "form-patients";
    patientRepository.save(patient);
    return "redirect:/index?page=" + page + "&keyword=" + keyword;
}
```

- **Validation Rules**:
  - `nom`: Must not be empty and between 4–10 characters.
  - `score`: Must be at least 100.

## Security
Spring Security is configured to secure the application with role-based access control.

### SecurityConfig
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("user2").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("admin").password(passwordEncoder.encode("1234")).roles("USER", "ADMIN").build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .formLogin(ar -> ar.loginPage("/login").defaultSuccessUrl("/index").permitAll())
                .rememberMe(rm -> rm.key("remember-me-key").tokenValiditySeconds(86400))
                .exceptionHandling(ar -> ar.accessDeniedPage("/notAuthorized"))
                .authorizeHttpRequests(ar -> ar
                        .requestMatchers("/webjars/**", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- **Features**:
  - **Authentication**: Custom login page (`/login`) with "remember me" functionality.
  - **Authorization**: Role-based access (`USER` for `/user/**`, `ADMIN` for `/admin/**`).
  - **Access Denied**: Redirects to `/notAuthorized` for unauthorized access.
  - **In-Memory Users**: Temporary user management (user1, user2, admin).

### Login Page
The `login.html` page includes a form with a "remember me" checkbox:

```html
<form method="post" th:action="@{/login}">
    <input type="text" name="username" placeholder="Username">
    <input type="password" name="password" placeholder="Password">
    <input type="checkbox" name="remember-me"> Remember me
    <button type="submit">Login</button>
</form>
```

### Not Authorized Page
A dedicated controller handles access-denied scenarios:

```java
@Controller
public class SecurityController {
    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }
}
```

## Custom User Management
To make user management persistent, the application supports database-backed authentication using custom `AppUser` and `AppRole` entities.

### Entities
```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {
    @Id
    private String userId;
    @Column(unique = true)
    private String username;
    private String email;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<AppRole> roles;
}

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppRole {
    @Id
    private String role;
}
```

### Repositories
```java
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
}

public interface AppRoleRepository extends JpaRepository<AppRole, String> {
}
```

### Account Service
The `AccountService` interface and its implementation handle user and role operations:

```java
public interface AccountService {
    AppUser addNewUser(String username, String password, String confirmPassword, String email);
    AppRole addNewRole(String role);
    void addRoleToUser(String username, String role);
    void removeRoleFromUser(String username, String role);
    AppUser loadUserByUsername(String username);
}

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;
    // Implementation details
}
```

### User Details Service
A custom `UserDetailsService` integrates with the `AccountService`:

```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountService accountService;

    public UserDetailsServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = accountService.loadUserByUsername(username);
        if (appUser == null) throw new UsernameNotFoundException("User not found");
        return new org.springframework.security.core.userdetails.User(
                appUser.getUsername(),
                appUser.getPassword(),
                appUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toList())
        );
    }
}
```

### Database Initialization
Users and roles are seeded at startup using a `CommandLineRunner`:

```java
@Bean
CommandLineRunner commandLineRunnerUserDetails(AccountService accountService) {
    return args -> {
        accountService.addNewRole("USER");
        accountService.addNewRole("ADMIN");
        accountService.addNewUser("user1", "1234", "1234", "user1@gmail.com");
        accountService.addNewUser("user2", "1234", "1234", "user2@gmail.com");
        accountService.addNewUser("admin", "1234", "1234", "admin@gmail.com");
        accountService.addRoleToUser("user1", "USER");
        accountService.addRoleToUser("user2", "USER");
        accountService.addRoleToUser("admin", "USER");
        accountService.addRoleToUser("admin", "ADMIN");
    };
}
```

### Configuration
Update `application.properties` to enable JPA schema generation:

```properties
spring.jpa.hibernate.ddl-auto=update
```

The `SecurityConfig` is updated to use the custom `UserDetailsService`:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, UserDetailsService userDetailsServiceImpl) throws Exception {
    return httpSecurity
            .formLogin(ar -> ar.loginPage("/login").defaultSuccessUrl("/index").permitAll())
            .rememberMe(rm -> rm.key("remember-me-key").tokenValiditySeconds(86400))
            .exceptionHandling(ar -> ar.accessDeniedPage("/notAuthorized"))
            .userDetailsService(userDetailsServiceImpl)
            .authorizeHttpRequests(ar -> ar
                    .requestMatchers("/webjars/**", "/css/**", "/js/**", "/h2-console/**").permitAll()
                    .requestMatchers("/user/**").hasRole("USER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .build();
}
```

## Setup and Running
1. **Prerequisites**:
   - Java 17 or later.
   - Maven 3.6+.
   - IDE (e.g., IntelliJ IDEA, Eclipse) or command line.

2. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd gestiondespatients
   ```

3. **Build the Project**:
   ```bash
   mvn clean install
   ```

4. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

5. **Access the Application**:
   - Open `http://localhost:8088` in a browser.
   - Use the H2 console at `http://localhost:8088/h2-console` (username: `sa`, password: empty).
   - Login with credentials (e.g., `user1/1234`, `admin/1234`).

6. **Database Migration (Future)**:
   - Update `application.properties` for MySQL/MariaDB.
   - Example configuration:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/hospital
     spring.datasource.username=root
     spring.datasource.password=
     spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
     spring.jpa.hibernate.ddl-auto=update
     ```



