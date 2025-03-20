package com.laboratorio.laboratorio_reservas.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.laboratorio.laboratorio_reservas.models.Laboratorio;
import com.laboratorio.laboratorio_reservas.models.Reserva;
import com.laboratorio.laboratorio_reservas.repositories.LaboratorioRepository;
import com.laboratorio.laboratorio_reservas.repositories.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

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
        MockitoAnnotations.openMocks(this);
        reserva = new Reserva("lab1", "usuario1", new Date(), "08:00", "10:00", "Estudio", "Confirmada");
        reserva.setId("1");
        
        laboratorio = new Laboratorio("Lab 1", 30, "Edificio A", true);
        laboratorio.setId("lab1");
    }

    @Test
    void testConsultarReservaExistente() {
        when(reservaRepository.findAll()).thenReturn(Collections.singletonList(reserva));

        List<Reserva> reservas = reservaService.listarReservas();

        assertFalse(reservas.isEmpty());
        assertEquals("1", reservas.get(0).getId());
    }

    @Test
    void testConsultarReservaNoExistente() {
        when(reservaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Reserva> reservas = reservaService.listarReservas();

        assertTrue(reservas.isEmpty());
    }

    @Test
    void testCrearReserva() {
        when(laboratorioRepository.findById("lab1")).thenReturn(Optional.of(laboratorio));
        when(reservaRepository.findByIdLaboratorioAndFecha(anyString(), any(Date.class))).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        Reserva nuevaReserva = reservaService.crearReserva(reserva);

        assertNotNull(nuevaReserva);
        assertEquals("1", nuevaReserva.getId());
        assertEquals("Confirmada", nuevaReserva.getEstado());
    }

    @Test
    void testCancelarReserva() {
        when(reservaRepository.findById("1")).thenReturn(Optional.of(reserva));

        reservaService.cancelarReserva("1");

        assertEquals("Cancelada", reserva.getEstado());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void testEliminarReserva() {
        when(reservaRepository.existsById("1")).thenReturn(true);
        doNothing().when(reservaRepository).deleteById("1");

        reservaService.eliminarReserva("1");

        verify(reservaRepository, times(1)).deleteById("1");
    }

    @Test
    void testEliminarYConsultarReserva() {
        when(reservaRepository.existsById("1")).thenReturn(true);
        doNothing().when(reservaRepository).deleteById("1");
        when(reservaRepository.findAll()).thenReturn(Collections.emptyList());

        reservaService.eliminarReserva("1");
        List<Reserva> reservas = reservaService.listarReservas();

        assertTrue(reservas.isEmpty());
    }
}
