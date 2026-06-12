package com.saberpro.config;

import com.saberpro.entity.*;
import com.saberpro.repository.BeneficioRepository;
import com.saberpro.repository.FacultadRepository;
import com.saberpro.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga datos iniciales la primera vez que arranca la app (si no existen).
 * Como nadie se autoregistra, aquí se crean los usuarios base para poder entrar.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final FacultadRepository facultadRepository;
    private final BeneficioRepository beneficioRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           FacultadRepository facultadRepository,
                           BeneficioRepository beneficioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.facultadRepository = facultadRepository;
        this.beneficioRepository = beneficioRepository;
    }

    @Override
    public void run(String... args) {

        // Facultades de ejemplo
        if (facultadRepository.count() == 0) {
            crearFacultad("Tecnología en Desarrollo de Software", TipoFacultad.TECNOLOGIA);
            crearFacultad("Ingeniería de Sistemas", TipoFacultad.PROFESIONAL);
            crearFacultad("Ingeniería Industrial", TipoFacultad.PROFESIONAL);
        }

        // Cuentas del sistema FIJAS: una de admin y una de coordinación.
        // Se garantizan en cada arranque (si faltan, se recrean) y no se pueden borrar.
        // Contraseñas seguras (no aparecen en filtraciones, evitan la alerta del navegador).
        asegurarUsuarioSistema("admin", "Saber.Admin2025", "Administrador General", Rol.ADMINISTRADOR);
        asegurarUsuarioSistema("coordinacion", "Saber.Coord2025", "Coordinación Académica", Rol.COORDINACION);

        // Resolución de beneficios (Acuerdo 01-009 del 22/04/2024 - UTS).
        // Son FIJOS: se restablecen en cada arranque para que no puedan alterarse.
        {
            beneficioRepository.deleteAll();
            // --- Saber T&T (Tecnología) · Seminario de Grado II ---
            crearBeneficio(120, 150, TipoFacultad.TECNOLOGIA, "4.5",   0, "Seminario de Grado II");
            crearBeneficio(151, 170, TipoFacultad.TECNOLOGIA, "4.7",  50, "Seminario de Grado II");
            crearBeneficio(171, 300, TipoFacultad.TECNOLOGIA, "5.0", 100, "Seminario de Grado II");

            // --- Saber Pro (Profesional / Ingeniería) · Seminario de Grado IV ---
            crearBeneficio(180, 210, TipoFacultad.PROFESIONAL, "4.5",   0, "Seminario de Grado IV");
            crearBeneficio(211, 240, TipoFacultad.PROFESIONAL, "4.7",  50, "Seminario de Grado IV");
            crearBeneficio(241, 300, TipoFacultad.PROFESIONAL, "5.0", 100, "Seminario de Grado IV");
        }
    }

    private void crearFacultad(String nombre, TipoFacultad tipo) {
        Facultad f = new Facultad();
        f.setNombre(nombre);
        f.setTipo(tipo);
        facultadRepository.save(f);
    }

    private void crearUsuario(String username, String password, String nombre, Rol rol) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(password);
        u.setNombre(nombre);
        u.setRol(rol);
        u.setActivo(true);
        usuarioRepository.save(u);
    }

    /** Crea la cuenta de sistema si no existe; si existe, le asegura la contraseña fija y que esté activa. */
    private void asegurarUsuarioSistema(String username, String password, String nombre, Rol rol) {
        Usuario u = usuarioRepository.findByUsername(username).orElse(null);
        if (u == null) {
            crearUsuario(username, password, nombre, rol);
        } else {
            u.setPassword(password);
            u.setRol(rol);
            u.setActivo(true);
            usuarioRepository.save(u);
        }
    }

    private void crearBeneficio(int min, int max, TipoFacultad tipo,
                                String notaGrado, int beca, String seminario) {
        Beneficio b = new Beneficio();
        // Nombre según el nivel del estímulo
        String nombre;
        if (beca >= 100)      nombre = "Exoneración (nota 5.0) + Beca 100%";
        else if (beca >= 50)  nombre = "Exoneración (nota 4.7) + Beca 50%";
        else                  nombre = "Exoneración de trabajo de grado (nota 4.5)";
        b.setNombre(nombre);
        b.setDescripcion("Se exime el informe final de trabajo de grado (concepto aprobado) o se exonera el "
                + seminario + " con nota " + notaGrado
                + (beca > 0 ? ". Beca del " + beca + "% del derecho pecuniario de grado." : "."));
        b.setPuntajeMinimo(min);
        b.setPuntajeMaximo(max);
        b.setTipo(tipo);
        b.setNotaGrado(notaGrado);
        b.setBecaPorcentaje(beca);
        b.setSeminario(seminario);
        b.setVigencia("1 año desde la divulgación del ICFES");
        beneficioRepository.save(b);
    }
}
