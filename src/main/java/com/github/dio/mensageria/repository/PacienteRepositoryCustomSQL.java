package com.github.dio.mensageria.repository;

import com.github.dio.mensageria.model.FiltroPaciente;
import com.github.dio.mensageria.model.Paciente;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ‘Interface’ para implementação personalizada, no caso ela foi criada para fazer um método de filtragem.
 * @hidden
 * @author diogenesssantos
 */
@Repository
public interface PacienteRepositoryCustomSQL {
    /**
     * Método usamos {@link FiltroPaciente} para buscar os dados no banco de dados.
     *
     * @param filtroPaciente o filtro paciente
     * @return a list<Paciente>
     */
    List<Paciente> filtrar(FiltroPaciente filtroPaciente);

}
