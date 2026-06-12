package com.saberpro.entity;

/**
 * Tipo de programa. El puntaje mínimo para "pasar" depende del tipo:
 *  - TECNOLOGIA: pasa con >= 90
 *  - PROFESIONAL (ingeniería): pasa con >= 120
 */
public enum TipoFacultad {

    TECNOLOGIA("Tecnología", 90),
    PROFESIONAL("Profesional / Ingeniería", 120);

    private final String descripcion;
    private final int puntajeMinimoAprobacion;

    TipoFacultad(String descripcion, int puntajeMinimoAprobacion) {
        this.descripcion = descripcion;
        this.puntajeMinimoAprobacion = puntajeMinimoAprobacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPuntajeMinimoAprobacion() {
        return puntajeMinimoAprobacion;
    }
}
