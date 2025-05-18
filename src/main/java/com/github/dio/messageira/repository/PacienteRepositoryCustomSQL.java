package com.github.dio.messageira.repository;

import com.github.dio.messageira.model.FiltroPaciente;
import com.github.dio.messageira.model.Paciente;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepositoryCustomSQL {

    List<Paciente> filtrar(FiltroPaciente filtroPaciente);

}
