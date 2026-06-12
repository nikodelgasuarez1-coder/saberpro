package com.saberpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Resolución de beneficios: define qué beneficio (beca, descuento, reconocimiento)
 * recibe un estudiante según su rango de puntaje y el tipo de programa.
 *
 * NOTA: los rangos exactos los define la resolución oficial que entregará el usuario.
 * Esta entidad permite cargarlos/editarlos desde el sistema.
 */
@Entity
@Table(name = "beneficios")
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del beneficio es obligatorio")
    @Size(max = 120)
    private String nombre;

    @Size(max = 400)
    private String descripcion;

    @NotNull(message = "El puntaje mínimo es obligatorio")
    private Integer puntajeMinimo;

    @NotNull(message = "El puntaje máximo es obligatorio")
    private Integer puntajeMaximo;

    /** A qué tipo de programa aplica el beneficio (tecnología o profesional/ingeniería). */
    @NotNull(message = "El tipo de programa es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoFacultad tipo;

    // ----- Componentes del estímulo (Acuerdo 01-009) -----
    /** Nota con la que se exonera el Seminario de Grado (4.5 / 4.7 / 5.0). */
    private String notaGrado;
    /** Porcentaje de beca sobre el derecho pecuniario de grado (0, 50, 100). */
    private Integer becaPorcentaje;
    /** Espacio académico que se exonera (Seminario de Grado II / IV). */
    private String seminario;
    /** Vigencia del incentivo. */
    private String vigencia;

    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public String getNotaGrado()             { return notaGrado; }
    public void setNotaGrado(String notaGrado) { this.notaGrado = notaGrado; }

    public Integer getBecaPorcentaje()           { return becaPorcentaje; }
    public void setBecaPorcentaje(Integer becaPorcentaje) { this.becaPorcentaje = becaPorcentaje; }

    public String getSeminario()             { return seminario; }
    public void setSeminario(String seminario) { this.seminario = seminario; }

    public String getVigencia()              { return vigencia; }
    public void setVigencia(String vigencia) { this.vigencia = vigencia; }

    public String getNombre()                { return nombre; }
    public void setNombre(String nombre)     { this.nombre = nombre; }

    public String getDescripcion()               { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getPuntajeMinimo()            { return puntajeMinimo; }
    public void setPuntajeMinimo(Integer puntajeMinimo) { this.puntajeMinimo = puntajeMinimo; }

    public Integer getPuntajeMaximo()            { return puntajeMaximo; }
    public void setPuntajeMaximo(Integer puntajeMaximo) { this.puntajeMaximo = puntajeMaximo; }

    public TipoFacultad getTipo()            { return tipo; }
    public void setTipo(TipoFacultad tipo)   { this.tipo = tipo; }

    @Transient
    public boolean aplicaA(int puntaje, TipoFacultad tipoFacultad) {
        return tipo == tipoFacultad
                && puntajeMinimo != null && puntajeMaximo != null
                && puntaje >= puntajeMinimo && puntaje <= puntajeMaximo;
    }
}
