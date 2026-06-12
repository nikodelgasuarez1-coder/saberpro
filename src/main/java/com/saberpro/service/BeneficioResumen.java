package com.saberpro.service;

import com.saberpro.entity.Beneficio;
import com.saberpro.entity.Estudiante;

import java.util.List;

/**
 * Resumen de beneficios de un estudiante:
 *  - obtenidos: el beneficio que ya alcanzó (0 o 1, porque los rangos no se solapan).
 *  - siguiente: el próximo beneficio al que podría llegar.
 *  - faltan: cuántos puntos le faltan para ese siguiente beneficio.
 */
public class BeneficioResumen {

    private final Estudiante estudiante;
    private final List<Beneficio> obtenidos;
    private final Beneficio siguiente;
    private final int faltan;

    public BeneficioResumen(Estudiante estudiante, List<Beneficio> obtenidos,
                            Beneficio siguiente, int faltan) {
        this.estudiante = estudiante;
        this.obtenidos = obtenidos;
        this.siguiente = siguiente;
        this.faltan = faltan;
    }

    public Estudiante getEstudiante() { return estudiante; }
    public List<Beneficio> getObtenidos() { return obtenidos; }
    public Beneficio getSiguiente() { return siguiente; }
    public int getFaltan() { return faltan; }

    public boolean isTieneBeneficio() {
        return obtenidos != null && !obtenidos.isEmpty();
    }

    /** El beneficio principal (único) que alcanzó, o null. */
    public Beneficio getPrincipal() {
        return isTieneBeneficio() ? obtenidos.get(0) : null;
    }
}
