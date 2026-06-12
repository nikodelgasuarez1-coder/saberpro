package com.saberpro.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Recibo de pago Saber Pro que sube el estudiante.
 * Coordinación lo revisa y lo aprueba/rechaza.
 */
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "estudiante_id", unique = true)
    private Estudiante estudiante;

    /** Nombre del archivo del recibo guardado en el servidor. */
    private String archivo;

    /** Nombre original que subió el estudiante (para mostrar/descargar). */
    private String nombreOriginal;

    private LocalDateTime fechaSubida;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public Estudiante getEstudiante()                { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public String getArchivo()           { return archivo; }
    public void setArchivo(String archivo) { this.archivo = archivo; }

    public String getNombreOriginal()            { return nombreOriginal; }
    public void setNombreOriginal(String nombreOriginal) { this.nombreOriginal = nombreOriginal; }

    public LocalDateTime getFechaSubida()            { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

    public EstadoPago getEstado()            { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
}
