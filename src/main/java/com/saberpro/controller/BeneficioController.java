package com.saberpro.controller;

import com.saberpro.entity.TipoFacultad;
import com.saberpro.repository.BeneficioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Resolución de beneficios (Acuerdo 01-009). SOLO LECTURA:
 * los beneficios son fijos, no se crean, editan ni eliminan. Se aplican
 * automáticamente a cada estudiante según su tipo de programa y su puntaje.
 */
@Controller
@RequestMapping("/admin/beneficios")
public class BeneficioController {

    @Autowired
    private BeneficioRepository beneficioRepository;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("tecnologia", beneficioRepository.findByTipo(TipoFacultad.TECNOLOGIA));
        model.addAttribute("profesional", beneficioRepository.findByTipo(TipoFacultad.PROFESIONAL));
        return "resolucion";
    }
}
