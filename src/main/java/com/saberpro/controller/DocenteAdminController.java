package com.saberpro.controller;

import com.saberpro.entity.Docente;
import com.saberpro.entity.Rol;
import com.saberpro.entity.Usuario;
import com.saberpro.repository.DocenteRepository;
import com.saberpro.repository.FacultadRepository;
import com.saberpro.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD de docentes (zona administrador).
 */
@Controller
@RequestMapping("/admin/docentes")
public class DocenteAdminController {

    @Autowired private DocenteRepository docenteRepository;
    @Autowired private FacultadRepository facultadRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("docentes", docenteRepository.findAll());
        return "admin/docente/listar";
    }

    @GetMapping("/insertar")
    public String insertar(Model model) {
        model.addAttribute("docente", new Docente());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/docente/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("docente") Docente docente,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/docente/formulario";
        }
        docenteRepository.save(docente);
        return "redirect:/admin/docentes/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("docente", docenteRepository.findById(id).get());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/docente/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        // Quitar el acceso de usuario vinculado al docente antes de borrarlo.
        usuarioRepository.findByDocenteId(id).ifPresent(usuarioRepository::delete);
        docenteRepository.deleteById(id);
        return "redirect:/admin/docentes/listar";
    }

    /**
     * Genera el acceso (usuario/contraseña) del docente.
     * Usuario y contraseña = su número de documento.
     */
    @GetMapping("/acceso/{id}")
    public String generarAcceso(@PathVariable Long id, RedirectAttributes ra) {
        Docente d = docenteRepository.findById(id).orElse(null);
        if (d == null) return "redirect:/admin/docentes/listar";

        if (usuarioRepository.findByDocenteId(id).isPresent()) {
            ra.addFlashAttribute("msgError", "Este docente ya tiene un acceso creado.");
            return "redirect:/admin/docentes/listar";
        }
        String login = d.getDocumento();
        if (login == null || login.isBlank()) {
            ra.addFlashAttribute("msgError", "El docente no tiene documento para generar el acceso.");
            return "redirect:/admin/docentes/listar";
        }
        if (usuarioRepository.existsByUsername(login)) {
            ra.addFlashAttribute("msgError", "Ya existe un usuario con el documento " + login + ".");
            return "redirect:/admin/docentes/listar";
        }

        Usuario u = new Usuario();
        u.setUsername(login);
        u.setPassword(login);
        u.setNombre(d.getNombreCompleto());
        u.setRol(Rol.DOCENTE);
        u.setActivo(true);
        u.setDocente(d);
        usuarioRepository.save(u);

        ra.addFlashAttribute("msgOk", "Acceso de docente creado. Usuario y contraseña: " + login);
        return "redirect:/admin/docentes/listar";
    }
}
