package com.github.dio.messageira.repository;

import com.github.dio.messageira.model.FiltroPaciente;
import com.github.dio.messageira.model.Paciente;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PacienteRepositoryImpl implements PacienteRepositoryCustomSQL {
    private static final Logger log = LoggerFactory.getLogger(PacienteRepositoryImpl.class);
    @Autowired
    private EntityManager entityManager;

    public List<Paciente> filtrar(FiltroPaciente filtroPaciente) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Paciente> querry = builder.createQuery(Paciente.class);
        Root<Paciente> root = querry.from(Paciente.class);
        List<Predicate> predicates = new ArrayList();
        if (filtroPaciente.getNome() != null) {
            predicates.add(builder.like(root.get("nome"), filtroPaciente.getNome() + "%"));
        }

        if (filtroPaciente.getBairro() != null) {
            predicates.add(builder.like(root.get("bairro"), filtroPaciente.getBairro() + "%"));
        }

        if (filtroPaciente.getDataMarcacaoInicial() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("dataMarcacao"), filtroPaciente.getDataMarcacaoInicial()));
        }

        if (filtroPaciente.getDataMarcacaoFinal() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("dataMarcacao"), filtroPaciente.getDataMarcacaoFinal()));
        }

        if (filtroPaciente.getTipoConsulta() != null) {
            predicates.add(builder.like(root.get("consulta"), filtroPaciente.getTipoConsulta() + "%"));
        }

        if (filtroPaciente.getMotivo() != null) {
            predicates.add(builder.and(new Predicate[]{builder.equal(root.get("motivo"), filtroPaciente.getMotivo())}));
        }

        querry.where((Predicate[])predicates.toArray(new Predicate[0]));
        TypedQuery<Paciente> pacienteTypedQuery = this.entityManager.createQuery(querry);
        return pacienteTypedQuery.getResultList();
    }
}
