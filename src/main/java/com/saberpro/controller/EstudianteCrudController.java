package com.saberpro.controller;

import com.saberpro.entity.Estudiante;
import com.saberpro.entity.Rol;
import com.saberpro.entity.Usuario;
import com.saberpro.repository.EstudianteRepository;
import com.saberpro.repository.FacultadRepository;
import com.saberpro.repository.PagoRepository;
import com.saberpro.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD de estudiantes (zona Coordinación), incluyendo la generación de su
 * acceso (usuario/contraseña) para que el estudiante entre al sistema.
 */
@Controller
@RequestMapping("/coordinacion/estudiantes")
public class EstudianteCrudController {

    @Autowired private EstudianteRepository estudianteRepository;
    @Autowired private FacultadRepository facultadRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PagoRepository pagoRepository;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("estudiantes", estudianteRepository.findAll());
        return "coordinacion/estudiante/listar";
    }

    @GetMapping("/insertar")
    public String insertar(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "coordinacion/estudiante/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("estudiante") Estudiante estudiante,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("facultades", facultadRepository.findAll());
            return "coordinacion/estudiante/formulario";
        }
        estudianteRepository.save(estudiante);
        return "redirect:/coordinacion/estudiantes/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("estudiante", estudianteRepository.findById(id).get());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "coordinacion/estudiante/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        // Antes de borrar el estudiante, eliminar lo que lo referencia
        // (su acceso de usuario y su pago) para no violar las llaves foráneas.
        usuarioRepository.findByEstudianteId(id).ifPresent(usuarioRepository::delete);
        pagoRepository.findByEstudianteId(id).ifPresent(pagoRepository::delete);
        estudianteRepository.deleteById(id); // el resultado se borra en cascada
        return "redirect:/coordinacion/estudiantes/listar";
    }

    /**
     * Genera el acceso (usuario/contraseña) para el estudiante.
     * Usuario = número de documento (o registro si no hay documento).
     * Contraseña inicial = el mismo identificador.
     */
    @GetMapping("/acceso/{id}")
    public String generarAcceso(@PathVariable Long id, RedirectAttributes ra) {
        Estudiante est = estudianteRepository.findById(id).orElse(null);
        if (est == null) {
            return "redirect:/coordinacion/estudiantes/listar";
        }

        String login = est.getNumeroDocumento();
        if (login == null || login.isBlank()) login = est.getNumeroRegistro();

        if (login == null || login.isBlank()) {
            ra.addFlashAttribute("msgError", "El estudiante no tiene documento ni registro para generar el acceso.");
            return "redirect:/coordinacion/estudiantes/listar";
        }
        if (usuarioRepository.existsByUsername(login)) {
            ra.addFlashAttribute("msgError", "Ya existe un acceso con el usuario " + login + ".");
            return "redirect:/coordinacion/estudiantes/listar";
        }

        Usuario u = new Usuario();
        u.setUsername(login);
        u.setPassword(login);
        u.setNombre(est.getNombreCompleto());
        u.setRol(Rol.ESTUDIANTE);
        u.setActivo(true);
        u.setEstudiante(est);
        usuarioRepository.save(u);

        ra.addFlashAttribute("msgOk", "Acceso creado. Usuario y contraseña: " + login);
        return "redirect:/coordinacion/estudiantes/listar";
    }
}
