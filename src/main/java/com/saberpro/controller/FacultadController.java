package com.saberpro.controller;

import com.saberpro.entity.Facultad;
import com.saberpro.entity.TipoFacultad;
import com.saberpro.repository.FacultadRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/facultades")
public class FacultadController {

    @Autowired
    private FacultadRepository facultadRepository;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/facultad/listar";
    }

    @GetMapping("/insertar")
    public String insertar(Model model) {
        model.addAttribute("facultad", new Facultad());
        model.addAttribute("tipos", TipoFacultad.values());
        return "admin/facultad/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("facultad") Facultad facultad,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoFacultad.values());
            return "admin/facultad/formulario";
        }
        facultadRepository.save(facultad);
        return "redirect:/admin/facultades/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("facultad", facultadRepository.findById(id).get());
        model.addAttribute("tipos", TipoFacultad.values());
        return "admin/facultad/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            facultadRepository.deleteById(id);
            ra.addFlashAttribute("msgOk", "Facultad eliminada correctamente.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // La facultad tiene directores/docentes/estudiantes asociados.
            ra.addFlashAttribute("msgError",
                "No se puede eliminar esta facultad porque tiene directores, docentes o estudiantes asociados. "
                + "Reasigna o elimina primero esos registros.");
        }
        return "redirect:/admin/facultades/listar";
    }
}
