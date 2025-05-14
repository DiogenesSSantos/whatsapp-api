package com.github.dio.messageira.repository;

import com.github.dio.messageira.model.Filtro;
import com.github.dio.messageira.model.Paciente;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.criteria.spi.CriteriaBuilderExtension;
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

    public List<Paciente> filtrar(Filtro filtro) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Paciente> querry = builder.createQuery(Paciente.class);
        Root<Paciente> root = querry.from(Paciente.class);
        List<Predicate> predicates = new ArrayList();
        if (filtro.getNome() != null) {
            predicates.add(builder.like(root.get("nome"), filtro.getNome() + "%"));
        }

        if (filtro.getBairro() != null) {
            predicates.add(builder.like(root.get("bairro"), filtro.getBairro() + "%"));
        }

        if (filtro.getDataMarcacaoInicial() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("dataMarcacao"), filtro.getDataMarcacaoInicial()));
        }

        if (filtro.getDataMarcacaoFinal() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("dataMarcacao"), filtro.getDataMarcacaoFinal()));
        }

        if (filtro.getTipoConsulta() != null) {
            predicates.add(builder.like(root.get("consulta"), filtro.getTipoConsulta() + "%"));
        }

        if (filtro.getMotivo() != null) {
            predicates.add(builder.and(new Predicate[]{builder.equal(root.get("motivo"), filtro.getMotivo())}));
        }

        querry.where((Predicate[])predicates.toArray(new Predicate[0]));
        TypedQuery<Paciente> pacienteTypedQuery = this.entityManager.createQuery(querry);
        return pacienteTypedQuery.getResultList();
    }
}
