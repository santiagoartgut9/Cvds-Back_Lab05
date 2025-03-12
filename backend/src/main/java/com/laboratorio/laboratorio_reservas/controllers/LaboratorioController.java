package com.laboratorio.laboratorio_reservas.controllers;

import com.laboratorio.laboratorio_reservas.models.Laboratorio;
import com.laboratorio.laboratorio_reservas.services.LaboratorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/laboratorios")
public class LaboratorioController {

    @Autowired
    private LaboratorioService laboratorioService;

    @GetMapping
    public List<Laboratorio> obtenerLaboratorios() {
        return laboratorioService.listarLaboratorios();
    }
}
