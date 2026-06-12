package com.saberpro.controller;

import com.saberpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private FacultadRepository facultadRepository;
    @Autowired private DirectorRepository directorRepository;
    @Autowired private DocenteRepository docenteRepository;
    @Autowired private BeneficioRepository beneficioRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalFacultades", facultadRepository.count());
        model.addAttribute("totalDirectores", directorRepository.count());
        model.addAttribute("totalDocentes", docenteRepository.count());
        model.addAttribute("totalBeneficios", beneficioRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        return "admin/dashboard";
    }
}
