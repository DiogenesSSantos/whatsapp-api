package com.github.dio.messageira.repository;

import com.github.dio.messageira.model.Filtro;
import com.github.dio.messageira.model.Paciente;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepositoryCustomSQL {

    List<Paciente> filtrar(Filtro filtro);

}
