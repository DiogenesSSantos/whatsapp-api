package com.github.dio.mensageria.repository;

import com.github.dio.mensageria.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Paciente repository.
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente , Long> , PacienteRepositoryCustomSQL {

    /**
     * Find bycodigo paciente.
     *
     * @param codigoUUID the codigo uuid
     * @return the paciente
     */
    Paciente findBycodigo(String codigoUUID);

}
