package com.saberpro.controller;

import com.saberpro.entity.Director;
import com.saberpro.repository.DirectorRepository;
import com.saberpro.repository.FacultadRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/directores")
public class DirectorController {

    @Autowired private DirectorRepository directorRepository;
    @Autowired private FacultadRepository facultadRepository;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("directores", directorRepository.findAll());
        return "admin/director/listar";
    }

    @GetMapping("/insertar")
    public String insertar(Model model) {
        model.addAttribute("director", new Director());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/director/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("director") Director director,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/director/formulario";
        }
        directorRepository.save(director);
        return "redirect:/admin/directores/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("director", directorRepository.findById(id).get());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/director/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        directorRepository.deleteById(id);
        return "redirect:/admin/directores/listar";
    }
}
