package com.saberpro.repository;

import com.saberpro.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByNumeroDocumento(String numeroDocumento);

    Optional<Estudiante> findByNumeroRegistro(String numeroRegistro);

    List<Estudiante> findByFacultadId(Long facultadId);

    List<Estudiante> findByAprobadoSaberPro(boolean aprobadoSaberPro);
}
