package com.saberpro.repository;

import com.saberpro.entity.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, Long> {

    Optional<Resultado> findByEstudianteId(Long estudianteId);
}
