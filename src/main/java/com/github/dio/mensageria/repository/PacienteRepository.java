package com.github.dio.mensageria.repository;

import com.github.dio.mensageria.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Classe, repositório para persistência do {@link Paciente}
 * @hidden
 * @author diogenesssantos
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente , Long> , PacienteRepositoryCustomSQL {
    /**
     * Método para buscar o usuário pelo UUID.
     *
     * @param codigoUUID o código uuid
     * @return o paciente.
     */
    Paciente findBycodigo(String codigoUUID);

}
