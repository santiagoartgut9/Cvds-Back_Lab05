package com.laboratorio.laboratorio_reservas.repositories;

import com.laboratorio.laboratorio_reservas.models.Reserva;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;  // <-- Falta este import
import java.util.List;  // <-- Falta este import

@Repository
public interface ReservaRepository extends MongoRepository<Reserva, String> {
    List<Reserva> findByIdLaboratorioAndFecha(String idLaboratorio, Date fecha);
}
