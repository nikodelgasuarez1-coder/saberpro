package com.saberpro.repository;

import com.saberpro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Usuario> findByEstudianteId(Long estudianteId);

    Optional<Usuario> findByDocenteId(Long docenteId);
}
