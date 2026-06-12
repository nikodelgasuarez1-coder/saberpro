package com.saberpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Usuario para el inicio de sesión. Nadie se autoregistra:
 * los crea el administrador (directores/docentes/coordinación) o coordinación (estudiantes).
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 60)
    @Column(unique = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 120)
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    private String nombre;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    private Rol rol;

    private boolean activo = true;

    /** Si el usuario es de rol ESTUDIANTE, queda enlazado a su ficha de estudiante. */
    @OneToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    /** Si el usuario es de rol DOCENTE, queda enlazado a su ficha de docente (y su facultad). */
    @OneToOne
    @JoinColumn(name = "docente_id")
    private Docente docente;

    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre()                { return nombre; }
    public void setNombre(String nombre)     { this.nombre = nombre; }

    public Rol getRol()              { return rol; }
    public void setRol(Rol rol)      { this.rol = rol; }

    public boolean isActivo()            { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Estudiante getEstudiante()                { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Docente getDocente()              { return docente; }
    public void setDocente(Docente docente)  { this.docente = docente; }
}
