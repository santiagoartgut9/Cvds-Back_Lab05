package com.laboratorio.laboratorio_reservas.services;

import com.laboratorio.laboratorio_reservas.models.Laboratorio;
import com.laboratorio.laboratorio_reservas.models.Reserva;
import com.laboratorio.laboratorio_reservas.repositories.LaboratorioRepository;
import com.laboratorio.laboratorio_reservas.repositories.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceUnitTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private LaboratorioRepository laboratorioRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private Laboratorio laboratorio;

    @BeforeEach
    void setUp() {
        reserva = new Reserva("lab1", "usuario1", new Date(), "08:00", "10:00", "Clase", "Pendiente");
        laboratorio = new Laboratorio("Lab 1", 30, "Edificio A", true);
        laboratorio.setId("lab1");
    }

    @Test
    void crearReserva_CuandoLaboratorioNoExiste_DebeLanzarExcepcion() {
        when(laboratorioRepository.findById("lab1")).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> reservaService.crearReserva(reserva));
        assertEquals("Laboratorio no encontrado", exception.getMessage());
    }

    @Test
    void crearReserva_CuandoLaboratorioNoDisponible_DebeLanzarExcepcion() {
        laboratorio.setEstado(false);
        when(laboratorioRepository.findById("lab1")).thenReturn(Optional.of(laboratorio));
        Exception exception = assertThrows(RuntimeException.class, () -> reservaService.crearReserva(reserva));
        assertEquals("Laboratorio no está disponible para reservas", exception.getMessage());
    }

    @Test
    void crearReserva_CuandoHayConflictoHorario_DebeLanzarExcepcion() {
        when(laboratorioRepository.findById("lab1")).thenReturn(Optional.of(laboratorio));
        when(reservaRepository.findByIdLaboratorioAndFecha(anyString(), any(Date.class)))
                .thenReturn(Arrays.asList(new Reserva("lab1", "usuario2", new Date(), "09:00", "11:00", "Otra clase", "Confirmada")));
        Exception exception = assertThrows(RuntimeException.class, () -> reservaService.crearReserva(reserva));
        assertEquals("Conflicto de horario: ya existe una reserva en esa franja horaria", exception.getMessage());
    }

    @Test
    void crearReserva_CuandoTodoValido_DebeGuardarReserva() {
        when(laboratorioRepository.findById("lab1")).thenReturn(Optional.of(laboratorio));
        when(reservaRepository.findByIdLaboratorioAndFecha(anyString(), any(Date.class))).thenReturn(List.of());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        Reserva resultado = reservaService.crearReserva(reserva);
        assertNotNull(resultado);
        assertEquals("Confirmada", resultado.getEstado());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void cancelarReserva_CuandoExiste_DebeActualizarEstado() {
        when(reservaRepository.findById("reserva1")).thenReturn(Optional.of(reserva));
        reservaService.cancelarReserva("reserva1");
        assertEquals("Cancelada", reserva.getEstado());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void cancelarReserva_CuandoNoExiste_DebeLanzarExcepcion() {
        when(reservaRepository.findById("reserva1")).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> reservaService.cancelarReserva("reserva1"));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }
}
