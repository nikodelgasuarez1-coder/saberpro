package com.saberpro.controller;

import com.saberpro.entity.Rol;
import com.saberpro.entity.Usuario;
import com.saberpro.repository.DocenteRepository;
import com.saberpro.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Gestión de usuarios del sistema (zona administrador).
 * Nadie se autoregistra. Las cuentas de ADMINISTRADOR y COORDINACION son
 * fijas del sistema: no se crean más, ni se editan ni eliminan desde aquí.
 * Por este CRUD solo se gestionan accesos de DOCENTE y ESTUDIANTE.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DocenteRepository docenteRepository;

    /** Roles que SÍ se pueden gestionar por este CRUD. */
    private static final Rol[] ROLES_GESTIONABLES = { Rol.DOCENTE, Rol.ESTUDIANTE };

    private boolean esCuentaSistema(Rol rol) {
        return rol == Rol.ADMINISTRADOR || rol == Rol.COORDINACION;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuario/listar";
    }

    @GetMapping("/insertar")
    public String insertar(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", ROLES_GESTIONABLES);
        model.addAttribute("docentes", docenteRepository.findAll());
        return "admin/usuario/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuario") Usuario usuario,
                          BindingResult result, Model model) {
        // Validar usuario único solo al crear
        if (usuario.getId() == null && usuarioRepository.existsByUsername(usuario.getUsername())) {
            result.rejectValue("username", "dup", "Ese nombre de usuario ya existe");
        }
        // No se permiten cuentas de administrador/coordinación por este CRUD
        if (esCuentaSistema(usuario.getRol())) {
            result.rejectValue("rol", "sys", "Solo puede haber un Administrador y una Coordinación (cuentas fijas del sistema).");
        }
        // No permitir convertir una cuenta del sistema existente
        if (usuario.getId() != null) {
            Usuario actual = usuarioRepository.findById(usuario.getId()).orElse(null);
            if (actual != null && esCuentaSistema(actual.getRol())) {
                return "redirect:/admin/usuarios/listar";
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", ROLES_GESTIONABLES);
            model.addAttribute("docentes", docenteRepository.findAll());
            return "admin/usuario/formulario";
        }
        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u == null) return "redirect:/admin/usuarios/listar";
        if (esCuentaSistema(u.getRol())) {
            ra.addFlashAttribute("msgError", "La cuenta de " + u.getRol() + " es del sistema y no se puede editar.");
            return "redirect:/admin/usuarios/listar";
        }
        model.addAttribute("usuario", u);
        model.addAttribute("roles", ROLES_GESTIONABLES);
        model.addAttribute("docentes", docenteRepository.findAll());
        return "admin/usuario/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null && esCuentaSistema(u.getRol())) {
            ra.addFlashAttribute("msgError", "La cuenta de " + u.getRol() + " es del sistema y no se puede eliminar.");
            return "redirect:/admin/usuarios/listar";
        }
        usuarioRepository.deleteById(id);
        ra.addFlashAttribute("msgOk", "Usuario eliminado correctamente.");
        return "redirect:/admin/usuarios/listar";
    }
}
