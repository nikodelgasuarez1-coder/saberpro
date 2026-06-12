package com.saberpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100)
    private String apellidos;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20)
    @Column(unique = true)
    private String documento;

    @Email(message = "Correo no válido")
    private String correo;

    @Size(max = 20)
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getNombres()               { return nombres; }
    public void setNombres(String nombres)   { this.nombres = nombres; }

    public String getApellidos()                 { return apellidos; }
    public void setApellidos(String apellidos)   { this.apellidos = apellidos; }

    public String getDocumento()                 { return documento; }
    public void setDocumento(String documento)   { this.documento = documento; }

    public String getCorreo()                { return correo; }
    public void setCorreo(String correo)     { this.correo = correo; }

    public String getTelefono()                  { return telefono; }
    public void setTelefono(String telefono)     { this.telefono = telefono; }

    public Facultad getFacultad()                { return facultad; }
    public void setFacultad(Facultad facultad)   { this.facultad = facultad; }

    @Transient
    public String getNombreCompleto() {
        return (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
    }
}
