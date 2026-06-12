package com.saberpro.controller;

import com.saberpro.entity.EstadoPago;
import com.saberpro.entity.Estudiante;
import com.saberpro.entity.Facultad;
import com.saberpro.entity.Pago;
import com.saberpro.repository.EstudianteRepository;
import com.saberpro.repository.FacultadRepository;
import com.saberpro.repository.PagoRepository;
import com.saberpro.service.BeneficioService;
import com.saberpro.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/coordinacion")
public class CoordinacionController {

    @Autowired private EstudianteRepository estudianteRepository;
    @Autowired private FacultadRepository facultadRepository;
    @Autowired private ExcelService excelService;
    @Autowired private BeneficioService beneficioService;
    @Autowired private PagoRepository pagoRepository;

    @GetMapping
    public String dashboard(Model model) {
        List<Estudiante> todos = estudianteRepository.findAll();
        long aprobados = todos.stream().filter(Estudiante::isAprobadoSaberPro).count();
        long conResultado = todos.stream().filter(e -> e.getResultado() != null).count();
        model.addAttribute("totalEstudiantes", todos.size());
        model.addAttribute("aprobados", aprobados);
        model.addAttribute("conResultado", conResultado);
        return "coordinacion/dashboard";
    }

    // ---------- Aprobar Saber Pro ----------
    @GetMapping("/aprobar")
    public String aprobar(Model model) {
        model.addAttribute("estudiantes", estudianteRepository.findAll());
        return "coordinacion/aprobar";
    }

    @GetMapping("/aprobar/{id}")
    public String toggleAprobar(@PathVariable Long id) {
        Estudiante est = estudianteRepository.findById(id).orElse(null);
        if (est != null) {
            est.setAprobadoSaberPro(!est.isAprobadoSaberPro());
            estudianteRepository.save(est);
        }
        return "redirect:/coordinacion/aprobar";
    }

    // ---------- Importar Excel ----------
    @GetMapping("/resultados/importar")
    public String importarForm(Model model) {
        model.addAttribute("facultades", facultadRepository.findAll());
        return "coordinacion/importar";
    }

    @PostMapping("/resultados/importar")
    public String importar(@RequestParam("archivo") MultipartFile archivo,
                           @RequestParam("facultadId") Long facultadId,
                           RedirectAttributes ra) {
        try {
            Facultad facultad = facultadRepository.findById(facultadId).orElse(null);
            if (archivo.isEmpty()) {
                ra.addFlashAttribute("msgError", "Debe seleccionar un archivo Excel.");
                return "redirect:/coordinacion/resultados/importar";
            }
            int n = excelService.importar(archivo, facultad);
            ra.addFlashAttribute("msgOk", "Se importaron/actualizaron " + n + " estudiantes.");
        } catch (Exception e) {
            ra.addFlashAttribute("msgError", "Error al importar: " + e.getMessage());
        }
        return "redirect:/coordinacion/estudiantes/listar";
    }

    // ---------- Exportar Excel ----------
    @GetMapping("/resultados/exportar")
    public ResponseEntity<byte[]> exportar() throws Exception {
        byte[] data = excelService.exportarInforme(estudianteRepository.findAll());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "informe_saber_pro.xlsx");
        return new ResponseEntity<>(data, headers, org.springframework.http.HttpStatus.OK);
    }

    // ---------- Ver resultado detallado de un estudiante ----------
    @GetMapping("/resultados/ver/{id}")
    public String verResultado(@PathVariable Long id, Model model) {
        Estudiante est = estudianteRepository.findById(id).orElse(null);
        model.addAttribute("estudiante", est);
        model.addAttribute("resumen", beneficioService.resumen(est));
        return "resultado/detalle";
    }

    // ---------- Informe de beneficios ----------
    @GetMapping("/informe/beneficios")
    public String informeBeneficios(Model model) {
        List<com.saberpro.service.BeneficioResumen> resumenes = estudianteRepository.findAll().stream()
                .map(beneficioService::resumen).collect(java.util.stream.Collectors.toList());
        model.addAttribute("resumenes", resumenes);
        return "informe/beneficios";
    }

    // ---------- Revisión de pagos ----------
    @GetMapping("/pagos")
    public String pagos(Model model) {
        model.addAttribute("pagos", pagoRepository.findAll());
        return "coordinacion/pagos";
    }

    @GetMapping("/pagos/aprobar/{id}")
    public String aprobarPago(@PathVariable Long id, RedirectAttributes ra) {
        cambiarEstadoPago(id, EstadoPago.APROBADO, ra);
        return "redirect:/coordinacion/pagos";
    }

    @GetMapping("/pagos/rechazar/{id}")
    public String rechazarPago(@PathVariable Long id, RedirectAttributes ra) {
        cambiarEstadoPago(id, EstadoPago.RECHAZADO, ra);
        return "redirect:/coordinacion/pagos";
    }

    private void cambiarEstadoPago(Long id, EstadoPago estado, RedirectAttributes ra) {
        Pago p = pagoRepository.findById(id).orElse(null);
        if (p != null) {
            p.setEstado(estado);
            pagoRepository.save(p);
            ra.addFlashAttribute("msgOk", "Pago marcado como " + estado + ".");
        }
    }
}
