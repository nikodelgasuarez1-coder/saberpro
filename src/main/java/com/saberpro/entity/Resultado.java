package com.saberpro.entity;

import jakarta.persistence.*;

/**
 * Resultados Saber Pro de un estudiante.
 * Guarda el puntaje y el nivel de cada uno de los módulos del Excel del ICFES.
 * Si el examen fue ANULADO no se muestran puntajes.
 */
@Entity
@Table(name = "resultados")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "estudiante_id", unique = true)
    private Estudiante estudiante;

    /** Examen anulado: no se muestran puntajes y se resalta en otro color. */
    private boolean anulado = false;

    // ----- Puntaje global -----
    private Integer puntajeGlobal;
    private String  nivelGlobal;

    // ----- Módulos genéricos -----
    private Integer comunicacionEscrita;
    private String  comunicacionEscritaNivel;

    private Integer razonamientoCuantitativo;
    private String  razonamientoCuantitativoNivel;

    private Integer lecturaCritica;
    private String  lecturaCriticaNivel;

    private Integer competenciasCiudadanas;
    private String  competenciasCiudadanasNivel;

    private Integer ingles;
    private String  inglesNivel;

    // ----- Módulos específicos (ingeniería / tecnología en sistemas) -----
    private Integer formulacionProyectos;
    private String  formulacionProyectosNivel;

    private Integer pensamientoCientifico;
    private String  pensamientoCientificoNivel;

    private Integer disenoSoftware;
    private String  disenoSoftwareNivel;

    /** Nivel de inglés MCER (A0, A1, A2, B1, B2). */
    private String nivelIngles;

    // =================== getters / setters ===================
    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public Estudiante getEstudiante()                { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public boolean isAnulado()           { return anulado; }
    public void setAnulado(boolean anulado) { this.anulado = anulado; }

    public Integer getPuntajeGlobal()            { return puntajeGlobal; }
    public void setPuntajeGlobal(Integer puntajeGlobal) { this.puntajeGlobal = puntajeGlobal; }

    public String getNivelGlobal()           { return nivelGlobal; }
    public void setNivelGlobal(String nivelGlobal) { this.nivelGlobal = nivelGlobal; }

    public Integer getComunicacionEscrita()          { return comunicacionEscrita; }
    public void setComunicacionEscrita(Integer v)    { this.comunicacionEscrita = v; }
    public String getComunicacionEscritaNivel()      { return comunicacionEscritaNivel; }
    public void setComunicacionEscritaNivel(String v) { this.comunicacionEscritaNivel = v; }

    public Integer getRazonamientoCuantitativo()        { return razonamientoCuantitativo; }
    public void setRazonamientoCuantitativo(Integer v)  { this.razonamientoCuantitativo = v; }
    public String getRazonamientoCuantitativoNivel()    { return razonamientoCuantitativoNivel; }
    public void setRazonamientoCuantitativoNivel(String v) { this.razonamientoCuantitativoNivel = v; }

    public Integer getLecturaCritica()            { return lecturaCritica; }
    public void setLecturaCritica(Integer v)      { this.lecturaCritica = v; }
    public String getLecturaCriticaNivel()        { return lecturaCriticaNivel; }
    public void setLecturaCriticaNivel(String v)  { this.lecturaCriticaNivel = v; }

    public Integer getCompetenciasCiudadanas()        { return competenciasCiudadanas; }
    public void setCompetenciasCiudadanas(Integer v)  { this.competenciasCiudadanas = v; }
    public String getCompetenciasCiudadanasNivel()    { return competenciasCiudadanasNivel; }
    public void setCompetenciasCiudadanasNivel(String v) { this.competenciasCiudadanasNivel = v; }

    public Integer getIngles()            { return ingles; }
    public void setIngles(Integer v)      { this.ingles = v; }
    public String getInglesNivel()        { return inglesNivel; }
    public void setInglesNivel(String v)  { this.inglesNivel = v; }

    public Integer getFormulacionProyectos()         { return formulacionProyectos; }
    public void setFormulacionProyectos(Integer v)   { this.formulacionProyectos = v; }
    public String getFormulacionProyectosNivel()     { return formulacionProyectosNivel; }
    public void setFormulacionProyectosNivel(String v) { this.formulacionProyectosNivel = v; }

    public Integer getPensamientoCientifico()        { return pensamientoCientifico; }
    public void setPensamientoCientifico(Integer v)  { this.pensamientoCientifico = v; }
    public String getPensamientoCientificoNivel()    { return pensamientoCientificoNivel; }
    public void setPensamientoCientificoNivel(String v) { this.pensamientoCientificoNivel = v; }

    public Integer getDisenoSoftware()           { return disenoSoftware; }
    public void setDisenoSoftware(Integer v)     { this.disenoSoftware = v; }
    public String getDisenoSoftwareNivel()       { return disenoSoftwareNivel; }
    public void setDisenoSoftwareNivel(String v) { this.disenoSoftwareNivel = v; }

    public String getNivelIngles()           { return nivelIngles; }
    public void setNivelIngles(String nivelIngles) { this.nivelIngles = nivelIngles; }

    // =================== lógica de negocio ===================

    /** Puntaje mínimo para aprobar según la facultad del estudiante (90 tec / 120 prof). */
    @Transient
    public int getUmbralAprobacion() {
        if (estudiante != null && estudiante.getFacultad() != null) {
            return estudiante.getFacultad().getPuntajeMinimoAprobacion();
        }
        return 120;
    }

    /** ¿El estudiante alcanzó el puntaje para pasar? */
    @Transient
    public boolean isAprobado() {
        return !anulado && puntajeGlobal != null && puntajeGlobal >= getUmbralAprobacion();
    }

    /**
     * Estado para mostrar y colorear en la vista:
     *  - "ANULADO"     → examen anulado (no se muestran puntajes).
     *  - "NO_ALCANZO"  → presentó bien pero quedó por debajo del umbral.
     *  - "APROBADO"    → alcanzó el puntaje requerido.
     */
    @Transient
    public String getEstado() {
        if (anulado) return "ANULADO";
        if (puntajeGlobal == null) return "SIN_RESULTADO";
        return isAprobado() ? "APROBADO" : "NO_ALCANZO";
    }

    /** Nivel de desempeño global según el puntaje (para mostrar como "Superior", etc.). */
    @Transient
    public String getNivelDesempeno() {
        if (anulado || puntajeGlobal == null) return "—";
        if (puntajeGlobal >= 225) return "Superior";
        if (puntajeGlobal >= 150) return "Alto";
        if (puntajeGlobal >= 100) return "Medio";
        return "Bajo";
    }

    // ===== Niveles automáticos por módulo (se calculan desde el puntaje) =====
    /** Calcula el Nivel 1–4 de un módulo a partir de su puntaje (escala Saber 0–300). */
    @Transient
    public String nivelDe(Integer puntaje) {
        if (puntaje == null) return "—";
        if (puntaje < 126)  return "Nivel 1";
        if (puntaje <= 155) return "Nivel 2";
        if (puntaje <= 195) return "Nivel 3";
        return "Nivel 4";
    }

    /** Devuelve el nivel guardado; si viene vacío, lo calcula automáticamente desde el puntaje. */
    private String nivelTexto(String guardado, Integer puntaje) {
        return (guardado != null && !guardado.isBlank()) ? guardado : nivelDe(puntaje);
    }

    @Transient public String getNivelGlobalTxt()                 { return nivelTexto(nivelGlobal, puntajeGlobal); }
    @Transient public String getComunicacionEscritaNivelTxt()    { return nivelTexto(comunicacionEscritaNivel, comunicacionEscrita); }
    @Transient public String getRazonamientoCuantitativoNivelTxt(){ return nivelTexto(razonamientoCuantitativoNivel, razonamientoCuantitativo); }
    @Transient public String getLecturaCriticaNivelTxt()         { return nivelTexto(lecturaCriticaNivel, lecturaCritica); }
    @Transient public String getCompetenciasCiudadanasNivelTxt() { return nivelTexto(competenciasCiudadanasNivel, competenciasCiudadanas); }
    @Transient public String getInglesNivelTxt()                 { return nivelTexto(inglesNivel, ingles); }
    @Transient public String getFormulacionProyectosNivelTxt()   { return nivelTexto(formulacionProyectosNivel, formulacionProyectos); }
    @Transient public String getPensamientoCientificoNivelTxt()  { return nivelTexto(pensamientoCientificoNivel, pensamientoCientifico); }
    @Transient public String getDisenoSoftwareNivelTxt()         { return nivelTexto(disenoSoftwareNivel, disenoSoftware); }
}
