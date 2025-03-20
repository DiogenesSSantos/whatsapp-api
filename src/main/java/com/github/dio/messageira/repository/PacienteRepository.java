package com.github.dio.messageira.repository;

import com.github.dio.messageira.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente , Long> , PacienteRepositoryCustomSQL {

    Paciente findBycodigo(String codigoUUID);

}
