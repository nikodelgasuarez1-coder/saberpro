package com.saberpro.repository;

import com.saberpro.entity.Beneficio;
import com.saberpro.entity.TipoFacultad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    List<Beneficio> findByTipo(TipoFacultad tipo);
}
