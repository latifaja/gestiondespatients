package com.emaple.gestiondespatients.web;

import com.emaple.gestiondespatients.entities.Patient;
import com.emaple.gestiondespatients.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {

    private PatientRepository patientRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(value = "page",defaultValue = "0") int numpage,
                        @RequestParam(value = "size",defaultValue = "4") int pagesize) {
        Page<Patient> pagepatients = patientRepository.findAll(PageRequest.of(numpage,pagesize));
        model.addAttribute("pages",new int[pagepatients.getTotalPages()]);
       // Java initializes all elements of an int[] array to 0 by default.
        model.addAttribute("listepatients", pagepatients.getContent());
model.addAttribute("currentpage",numpage);
    return "patients";
}



}
