package com.saberpro.service;

import com.saberpro.entity.Beneficio;
import com.saberpro.entity.Estudiante;
import com.saberpro.entity.Resultado;
import com.saberpro.entity.TipoFacultad;
import com.saberpro.repository.BeneficioRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calcula los beneficios que le corresponden a un estudiante según su puntaje
 * global y el tipo de programa, usando la resolución cargada en la BD.
 */
@Service
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;

    public BeneficioService(BeneficioRepository beneficioRepository) {
        this.beneficioRepository = beneficioRepository;
    }

    public List<Beneficio> beneficiosDe(Estudiante estudiante) {
        if (!aplicable(estudiante)) return Collections.emptyList();
        Resultado r = estudiante.getResultado();
        TipoFacultad tipo = estudiante.getFacultad().getTipo();
        int puntaje = r.getPuntajeGlobal();
        return beneficioRepository.findByTipo(tipo).stream()
                .filter(b -> b.aplicaA(puntaje, tipo))
                .collect(Collectors.toList());
    }

    /** Resumen con el beneficio obtenido, el siguiente y los puntos que faltan. */
    public BeneficioResumen resumen(Estudiante estudiante) {
        List<Beneficio> obtenidos = beneficiosDe(estudiante);
        Beneficio siguiente = null;
        int faltan = 0;

        if (aplicable(estudiante)) {
            int puntaje = estudiante.getResultado().getPuntajeGlobal();
            TipoFacultad tipo = estudiante.getFacultad().getTipo();
            siguiente = beneficioRepository.findByTipo(tipo).stream()
                    .filter(b -> b.getPuntajeMinimo() != null && b.getPuntajeMinimo() > puntaje)
                    .min(Comparator.comparingInt(Beneficio::getPuntajeMinimo))
                    .orElse(null);
            if (siguiente != null) {
                faltan = siguiente.getPuntajeMinimo() - puntaje;
            }
        }
        return new BeneficioResumen(estudiante, obtenidos, siguiente, faltan);
    }

    private boolean aplicable(Estudiante e) {
        return e != null && e.getFacultad() != null && e.getResultado() != null
                && !e.getResultado().isAnulado() && e.getResultado().getPuntajeGlobal() != null;
    }
}
