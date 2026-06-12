package com.saberpro.controller;

import com.saberpro.entity.Rol;
import com.saberpro.entity.Usuario;
import com.saberpro.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** Raíz: si hay sesión manda al panel del rol; si no, al login. */
    @GetMapping("/")
    public String inicio(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        return "redirect:" + panelPorRol(usuario.getRol());
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            return "redirect:" + panelPorRol(usuario.getRol());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<Usuario> opt = usuarioRepository.findByUsername(username);

        if (opt.isEmpty() || !opt.get().getPassword().equals(password)) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "login";
        }

        Usuario usuario = opt.get();
        if (!usuario.isActivo()) {
            model.addAttribute("error", "El usuario está inactivo. Contacte al administrador.");
            return "login";
        }

        session.setAttribute("usuario", usuario);
        return "redirect:" + panelPorRol(usuario.getRol());
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }

    private String panelPorRol(Rol rol) {
        return switch (rol) {
            case ADMINISTRADOR -> "/admin";
            case COORDINACION  -> "/coordinacion";
            case DOCENTE       -> "/docente";
            case ESTUDIANTE    -> "/estudiante";
        };
    }
}
