package com.directorio.oficios.directorio_consulta.service;

import com.directorio.oficios.directorio_consulta.model.Profesional;
import com.directorio.oficios.directorio_consulta.repository.ProfesionalRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProfesionalConsultaService {

    private final ProfesionalRepository profesionalRepository;

    public ProfesionalConsultaService(ProfesionalRepository profesionalRepository) {
        this.profesionalRepository = profesionalRepository;
    }

    public List<Profesional> buscarPorFiltros(String oficio, String ciudad) {

        if (oficio != null && ciudad != null) {
            return profesionalRepository.findByTipoOficioAndCiudadLocalidad(oficio, ciudad);

        } else if (oficio != null) {
            return profesionalRepository.findByTipoOficio(oficio);

        } else if (ciudad != null) {
            return profesionalRepository.findByCiudadLocalidad(ciudad);

        } else {

            return List.of();
        }
    }
}