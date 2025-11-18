package com.directorio.oficios.directorio_consulta.controller;

import com.directorio.oficios.directorio_consulta.model.Profesional;
import com.directorio.oficios.directorio_consulta.service.ProfesionalConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/consulta")
public class ProfesionalConsultaController {

    private final ProfesionalConsultaService consultaService;

    public ProfesionalConsultaController(ProfesionalConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    public ResponseEntity<List<Profesional>> buscar(
            @RequestParam(required = false) String oficio,
            @RequestParam(required = false) String ciudad) {

        if (oficio == null && ciudad == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Profesional> resultados = consultaService.buscarPorFiltros(oficio, ciudad);

        return ResponseEntity.ok(resultados);
    }
}