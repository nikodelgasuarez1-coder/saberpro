package com.saberpro.controller;

import com.saberpro.entity.Estudiante;
import com.saberpro.entity.EstadoPago;
import com.saberpro.entity.Pago;
import com.saberpro.entity.Usuario;
import com.saberpro.repository.EstudianteRepository;
import com.saberpro.repository.PagoRepository;
import com.saberpro.service.BeneficioService;
import jakarta.servlet.http.HttpSession;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Zona del estudiante: subir el recibo de pago y consultar su propio resultado.
 */
@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    @Autowired private PagoRepository pagoRepository;
    @Autowired private BeneficioService beneficioService;
    @Autowired private EstudianteRepository estudianteRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Estudiante estudianteDe(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuario");
        return u != null ? u.getEstudiante() : null;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        Estudiante est = estudianteDe(session);
        model.addAttribute("estudiante", est);
        if (est != null) {
            model.addAttribute("pago", pagoRepository.findByEstudianteId(est.getId()).orElse(null));
            model.addAttribute("beneficios", beneficioService.beneficiosDe(est));
            model.addAttribute("resumen", beneficioService.resumen(est));

            // Ranking por puntaje global (excluye anulados y sin resultado)
            List<Estudiante> ranking = estudianteRepository.findAll().stream()
                    .filter(e -> e.getResultado() != null
                            && !e.getResultado().isAnulado()
                            && e.getResultado().getPuntajeGlobal() != null)
                    .sorted(Comparator.comparingInt(
                            (Estudiante e) -> e.getResultado().getPuntajeGlobal()).reversed())
                    .collect(Collectors.toList());

            int posicion = 0;
            for (int i = 0; i < ranking.size(); i++) {
                if (ranking.get(i).getId().equals(est.getId())) { posicion = i + 1; break; }
            }
            model.addAttribute("ranking", ranking.stream().limit(8).collect(Collectors.toList()));
            model.addAttribute("posicion", posicion);
            model.addAttribute("totalRanking", ranking.size());
        }
        return "estudiante/dashboard";
    }

    // ----- Pago -----
    @GetMapping("/pago")
    public String pagoForm(HttpSession session, Model model) {
        Estudiante est = estudianteDe(session);
        model.addAttribute("estudiante", est);
        if (est != null) {
            model.addAttribute("pago", pagoRepository.findByEstudianteId(est.getId()).orElse(null));
        }
        return "estudiante/pago";
    }

    @PostMapping("/pago")
    public String subirPago(@RequestParam("archivo") MultipartFile archivo,
                            HttpSession session, RedirectAttributes ra) {
        Estudiante est = estudianteDe(session);
        if (est == null) {
            ra.addFlashAttribute("msgError", "Tu usuario no está vinculado a una ficha de estudiante.");
            return "redirect:/estudiante/pago";
        }
        if (archivo.isEmpty()) {
            ra.addFlashAttribute("msgError", "Debes seleccionar un archivo.");
            return "redirect:/estudiante/pago";
        }
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String original = archivo.getOriginalFilename();
            String nombre = "recibo_" + est.getId() + "_" + System.currentTimeMillis()
                    + extension(original);
            Files.copy(archivo.getInputStream(), dir.resolve(nombre));

            Pago pago = pagoRepository.findByEstudianteId(est.getId()).orElse(new Pago());
            pago.setEstudiante(est);
            pago.setArchivo(nombre);
            pago.setNombreOriginal(original);
            pago.setFechaSubida(LocalDateTime.now());
            pago.setEstado(EstadoPago.PENDIENTE);
            pagoRepository.save(pago);

            ra.addFlashAttribute("msgOk", "Recibo subido correctamente. Queda pendiente de revisión.");
        } catch (Exception e) {
            ra.addFlashAttribute("msgError", "Error al subir el archivo: " + e.getMessage());
        }
        return "redirect:/estudiante/pago";
    }

    // ----- Resultado propio -----
    @GetMapping("/resultado")
    public String resultado(HttpSession session, Model model) {
        Estudiante est = estudianteDe(session);
        model.addAttribute("estudiante", est);
        model.addAttribute("resumen", est != null ? beneficioService.resumen(est) : null);
        return "estudiante/resultado";
    }

    // ----- Mi Perfil -----
    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        model.addAttribute("estudiante", estudianteDe(session));
        return "estudiante/perfil";
    }

    @PostMapping("/perfil")
    public String guardarPerfil(@RequestParam String primerNombre,
                                @RequestParam(required = false) String segundoNombre,
                                @RequestParam String primerApellido,
                                @RequestParam(required = false) String segundoApellido,
                                @RequestParam(required = false) String correo,
                                @RequestParam(required = false) String telefono,
                                HttpSession session, RedirectAttributes ra) {
        Estudiante ses = estudianteDe(session);
        if (ses == null) return "redirect:/estudiante/perfil";

        Estudiante est = estudianteRepository.findById(ses.getId()).orElse(null);
        if (est == null) return "redirect:/estudiante/perfil";

        // Solo datos editables. El documento y el registro NO se tocan (son únicos).
        est.setPrimerNombre(primerNombre);
        est.setSegundoNombre(segundoNombre);
        est.setPrimerApellido(primerApellido);
        est.setSegundoApellido(segundoApellido);
        est.setCorreo(correo);
        est.setTelefono(telefono);
        estudianteRepository.save(est);

        // Refrescar la copia en sesión para que la UI muestre los cambios al instante.
        ses.setPrimerNombre(primerNombre);
        ses.setSegundoNombre(segundoNombre);
        ses.setPrimerApellido(primerApellido);
        ses.setSegundoApellido(segundoApellido);
        ses.setCorreo(correo);
        ses.setTelefono(telefono);

        ra.addFlashAttribute("msgOk", "Tus datos se actualizaron correctamente.");
        return "redirect:/estudiante/perfil";
    }

    private String extension(String nombre) {
        if (nombre == null) return "";
        int i = nombre.lastIndexOf('.');
        return i >= 0 ? nombre.substring(i) : "";
    }
}
