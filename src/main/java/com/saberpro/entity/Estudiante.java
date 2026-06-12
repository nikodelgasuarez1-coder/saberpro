package com.saberpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 10)
    private String tipoDocumento = "CC";

    @Size(max = 20)
    @Column(unique = true)
    private String numeroDocumento;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 80)
    private String primerApellido;

    @Size(max = 80)
    private String segundoApellido;

    @Size(max = 80)
    private String primerNombre;

    @Size(max = 80)
    private String segundoNombre;

    @Email(message = "Correo no válido")
    private String correo;

    @Size(max = 20)
    private String telefono;

    /** Número de registro del ICFES (ej. EK20183007722). */
    @Size(max = 30)
    @Column(unique = true)
    private String numeroRegistro;

    @ManyToOne
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    /** Aprobado por coordinación para presentar Saber Pro. */
    private boolean aprobadoSaberPro = false;

    @OneToOne(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private Resultado resultado;

    // ---- getters / setters ----
    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getTipoDocumento()                 { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento()               { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getPrimerApellido()                { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido()               { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getPrimerNombre()                  { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }

    public String getSegundoNombre()                 { return segundoNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }

    public String getCorreo()                { return correo; }
    public void setCorreo(String correo)     { this.correo = correo; }

    public String getTelefono()                  { return telefono; }
    public void setTelefono(String telefono)     { this.telefono = telefono; }

    public String getNumeroRegistro()                { return numeroRegistro; }
    public void setNumeroRegistro(String numeroRegistro) { this.numeroRegistro = numeroRegistro; }

    public Facultad getFacultad()                { return facultad; }
    public void setFacultad(Facultad facultad)   { this.facultad = facultad; }

    public boolean isAprobadoSaberPro()              { return aprobadoSaberPro; }
    public void setAprobadoSaberPro(boolean aprobadoSaberPro) { this.aprobadoSaberPro = aprobadoSaberPro; }

    public Resultado getResultado()              { return resultado; }
    public void setResultado(Resultado resultado) { this.resultado = resultado; }

    @Transient
    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null)   sb.append(primerNombre).append(" ");
        if (segundoNombre != null)  sb.append(segundoNombre).append(" ");
        if (primerApellido != null) sb.append(primerApellido).append(" ");
        if (segundoApellido != null) sb.append(segundoApellido);
        return sb.toString().trim();
    }
}
