package com.saberpro.controller;

import com.saberpro.entity.Estudiante;
import com.saberpro.entity.Facultad;
import com.saberpro.entity.Usuario;
import com.saberpro.repository.EstudianteRepository;
import com.saberpro.repository.FacultadRepository;
import com.saberpro.service.BeneficioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Zona Docente: consulta de informes por facultad o por cédula y beneficios.
 * Acceso de solo lectura.
 */
@Controller
@RequestMapping("/docente")
public class DocenteController {

    @Autowired private EstudianteRepository estudianteRepository;
    @Autowired private FacultadRepository facultadRepository;
    @Autowired private BeneficioService beneficioService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalEstudiantes", estudianteRepository.count());
        model.addAttribute("totalFacultades", facultadRepository.count());
        return "docente/dashboard";
    }

    // ----- Informe por facultad -----
    @GetMapping("/facultad")
    public String porFacultad(@RequestParam(required = false) Long facultadId,
                              HttpSession session, Model model) {
        List<Facultad> facultades = facultadRepository.findAll();
        model.addAttribute("facultades", facultades);

        // Cantidad de estudiantes por facultad (para mostrarlo en el desplegable)
        Map<Long, Integer> conteo = new LinkedHashMap<>();
        for (Facultad f : facultades) {
            conteo.put(f.getId(), estudianteRepository.findByFacultadId(f.getId()).size());
        }
        model.addAttribute("conteo", conteo);

        // Por defecto, muestra la facultad del propio docente (si la tiene)
        if (facultadId == null) {
            Usuario u = (Usuario) session.getAttribute("usuario");
            if (u != null && u.getDocente() != null && u.getDocente().getFacultad() != null) {
                facultadId = u.getDocente().getFacultad().getId();
            }
        }
        if (facultadId != null) {
            model.addAttribute("facultadId", facultadId);
            model.addAttribute("estudiantes", estudianteRepository.findByFacultadId(facultadId));
        }
        return "docente/facultad";
    }

    // ----- Búsqueda por cédula -----
    @GetMapping("/buscar")
    public String porCedula(@RequestParam(required = false) String documento, Model model) {
        if (documento != null && !documento.isBlank()) {
            Estudiante e = estudianteRepository.findByNumeroDocumento(documento.trim())
                    .orElseGet(() -> estudianteRepository.findByNumeroRegistro(documento.trim()).orElse(null));
            model.addAttribute("buscado", documento);
            model.addAttribute("estudiante", e);
            model.addAttribute("resumen", e != null ? beneficioService.resumen(e) : null);
        }
        return "docente/buscar";
    }

    // ----- Informe de beneficios -----
    @GetMapping("/informe/beneficios")
    public String informeBeneficios(Model model) {
        List<com.saberpro.service.BeneficioResumen> resumenes = estudianteRepository.findAll().stream()
                .map(beneficioService::resumen).collect(java.util.stream.Collectors.toList());
        model.addAttribute("resumenes", resumenes);
        return "informe/beneficios";
    }
}
