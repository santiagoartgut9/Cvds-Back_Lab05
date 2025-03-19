package com.laboratorio.laboratorio_reservas.services;

import com.laboratorio.laboratorio_reservas.models.Reserva;
import com.laboratorio.laboratorio_reservas.models.Laboratorio;
import com.laboratorio.laboratorio_reservas.repositories.ReservaRepository;
import com.laboratorio.laboratorio_reservas.repositories.LaboratorioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    public Reserva crearReserva(Reserva reserva) {
        Laboratorio laboratorio = laboratorioRepository.findById(reserva.getIdLaboratorio())
                .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
    
        if (!laboratorio.isEstado()) {
            throw new RuntimeException("Laboratorio no está disponible para reservas");
        }
    
        if (existeConflictoHorario(reserva)) {
            throw new RuntimeException("Conflicto de horario: ya existe una reserva en esa franja horaria");
        }
    
        reserva.setEstado("Confirmada");
        return reservaRepository.save(reserva);
    }

    private boolean existeConflictoHorario(Reserva nuevaReserva) {
        List<Reserva> reservasExistentes = reservaRepository.findByIdLaboratorioAndFecha(nuevaReserva.getIdLaboratorio(), nuevaReserva.getFecha());
    
        for (Reserva existente : reservasExistentes) {
            if (!"Cancelada".equalsIgnoreCase(existente.getEstado()) && hayCruceHorario(nuevaReserva, existente)) {
                return true;  // Hay conflicto
            }
        }
        return false;
    }

    private boolean hayCruceHorario(Reserva r1, Reserva r2) {
        return (r1.getHoraInicio().compareTo(r2.getHoraFin()) < 0) &&
               (r1.getHoraFin().compareTo(r2.getHoraInicio()) > 0);
    }

    public void cancelarReserva(String id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado("Cancelada");
        reservaRepository.save(reserva);
    }

    public void eliminarReserva(String id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada");
        }
        reservaRepository.deleteById(id);
    }

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }
}
