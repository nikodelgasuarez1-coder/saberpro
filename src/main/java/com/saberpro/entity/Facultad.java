package com.saberpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "facultades")
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoFacultad tipo;

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getNombre()                { return nombre; }
    public void setNombre(String nombre)     { this.nombre = nombre; }

    public TipoFacultad getTipo()                { return tipo; }
    public void setTipo(TipoFacultad tipo)       { this.tipo = tipo; }

    /** Puntaje mínimo para aprobar según el tipo de facultad (90 tec / 120 prof). */
    @Transient
    public int getPuntajeMinimoAprobacion() {
        return tipo != null ? tipo.getPuntajeMinimoAprobacion() : 120;
    }
}
