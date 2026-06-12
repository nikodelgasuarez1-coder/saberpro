package com.saberpro.controller;

import com.saberpro.entity.TipoFacultad;
import com.saberpro.repository.BeneficioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Vista de la Resolución de Beneficios (Acuerdo 01-009) accesible para todos
 * los roles (admin, coordinación, docente y estudiante). Solo lectura.
 */
@Controller
@RequestMapping("/resolucion-beneficios")
public class ResolucionController {

    @Autowired
    private BeneficioRepository beneficioRepository;

    @GetMapping("/ver")
    public String ver(Model model) {
        model.addAttribute("tecnologia", beneficioRepository.findByTipo(TipoFacultad.TECNOLOGIA));
        model.addAttribute("profesional", beneficioRepository.findByTipo(TipoFacultad.PROFESIONAL));
        return "resolucion";
    }
}
