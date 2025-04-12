package com.emaple.gestiondespatients.web;

import com.emaple.gestiondespatients.entities.Patient;
import com.emaple.gestiondespatients.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {

    private PatientRepository patientRepository;

    @GetMapping("/user/index")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int numpage,
                        @RequestParam(name = "size", defaultValue = "4") int pagesize,
                        @RequestParam(name = "keyword", defaultValue = "") String kw) {
        Page<Patient> pagepatients = patientRepository.findByNomContains(kw, PageRequest.of(numpage, pagesize));
        model.addAttribute("pages", new int[pagepatients.getTotalPages()]);
        // Java initializes all elements of an int[] array to 0 by default.
        model.addAttribute("listepatients", pagepatients.getContent());
        model.addAttribute("numcurrentpage", numpage);
        model.addAttribute("keyword", kw);
        return "patients";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/delete")
    public String delete(Long id, String keyword, int page) {
        patientRepository.deleteById(id);
        return "redirect:/user/index?page=" + page + "&keyword=" + keyword;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/formPatients")
    public String formPatients(Model model) {
        model.addAttribute("patient", new Patient());
        return "formPatients";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/save")
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult,
                       @RequestParam( defaultValue = "0") int page,
                       @RequestParam( defaultValue = "") String keyword) {
        if (bindingResult.hasErrors()) return "formPatients";
        patientRepository.save(patient);
        return "redirect:/user/index?page=" + page + "&keyword=" + keyword;
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/editPatient")

    public String editPatient(Model model, Long id, String keyword, int page) {

        Patient patient = patientRepository.findById(id).orElse(null);

        if (patient == null) throw new RuntimeException("Patient introuvable");

        model.addAttribute("patient", patient);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);

        return "editPatient";
    }

    @GetMapping("/")
    public String home() {

        return "redirect:/user/index";
    }
}