package com.directorio.oficios.directorio_consulta.repository;

import com.directorio.oficios.directorio_consulta.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {

    List<Profesional> findByTipoOficioAndCiudadLocalidad(String tipoOficio, String ciudadLocalidad);

    List<Profesional> findByTipoOficio(String tipoOficio);

    List<Profesional> findByCiudadLocalidad(String ciudadLocalidad);
}