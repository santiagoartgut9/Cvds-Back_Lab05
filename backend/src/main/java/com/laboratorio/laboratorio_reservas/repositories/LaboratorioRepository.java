package com.laboratorio.laboratorio_reservas.repositories;

import com.laboratorio.laboratorio_reservas.models.Laboratorio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratorioRepository extends MongoRepository<Laboratorio, String> {
}
