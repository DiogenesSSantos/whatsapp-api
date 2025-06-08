package com.github.dio.mensageria.repository;

import com.github.dio.mensageria.model.FiltroPaciente;
import com.github.dio.mensageria.model.Paciente;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Paciente repository custom sql.
 */
@Repository
public interface PacienteRepositoryCustomSQL {

    /**
     * Filtrar list.
     *
     * @param filtroPaciente the filtro paciente
     * @return the list
     */
    List<Paciente> filtrar(FiltroPaciente filtroPaciente);

}
