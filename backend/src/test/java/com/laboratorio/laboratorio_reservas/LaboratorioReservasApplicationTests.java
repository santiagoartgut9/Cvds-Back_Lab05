package com.laboratorio.laboratorio_reservas;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.laboratorio.laboratorio_reservas.models.Reserva;
import com.laboratorio.laboratorio_reservas.repositories.ReservaRepository;
import com.laboratorio.laboratorio_reservas.services.ReservaService;

@SpringBootTest
public class LaboratorioReservasApplicationTests {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @BeforeEach
    void setUp() {
        reservaRepository.deleteAll();
    }

    @Test
    void givenOneReserva_whenFindById_thenShouldReturnReserva() {
        Reserva reserva = new Reserva("lab1", "user1", new Date(), "08:00", "10:00", "Estudio", "Confirmada");
        Reserva saved = reservaService.crearReserva(reserva);

        List<Reserva> reservas = reservaRepository.findByIdLaboratorioAndFecha(saved.getIdLaboratorio(), saved.getFecha());
        assertFalse(reservas.isEmpty());
        assertEquals(saved.getId(), reservas.get(0).getId());
    }

    @Test
    void givenNoReservas_whenFindById_thenShouldReturnEmpty() {
        List<Reserva> reservas = reservaRepository.findByIdLaboratorioAndFecha("lab1", new Date());
        assertTrue(reservas.isEmpty());
    }

    @Test
    void givenNoReservas_whenCreate_thenShouldBeSuccessful() {
        Reserva reserva = new Reserva("lab1", "user1", new Date(), "08:00", "10:00", "Estudio", "Confirmada");
        Reserva saved = reservaService.crearReserva(reserva);
        assertNotNull(saved);
        assertEquals("Confirmada", saved.getEstado());
    }

    @Test
    void givenOneReserva_whenDelete_thenShouldBeSuccessful() {
        Reserva reserva = new Reserva("lab1", "user1", new Date(), "08:00", "10:00", "Estudio", "Confirmada");
        Reserva saved = reservaService.crearReserva(reserva);

        reservaService.cancelarReserva(saved.getId());
        Reserva updated = reservaRepository.findById(saved.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("Cancelada", updated.getEstado());
    }

    @Test
    void givenOneReserva_whenDeleteAndFind_thenShouldReturnEmpty() {
        Reserva reserva = new Reserva("lab1", "user1", new Date(), "08:00", "10:00", "Estudio", "Confirmada");
        Reserva saved = reservaService.crearReserva(reserva);

        reservaService.cancelarReserva(saved.getId());
        List<Reserva> reservas = reservaRepository.findByIdLaboratorioAndFecha("lab1", new Date());
        assertTrue(reservas.isEmpty() || reservas.get(0).getEstado().equals("Cancelada"));
    }
}