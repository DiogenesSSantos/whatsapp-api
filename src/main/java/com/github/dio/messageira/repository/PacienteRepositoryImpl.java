package com.github.dio.messageira.repository;

import com.github.dio.messageira.model.Paciente;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.query.criteria.spi.CriteriaBuilderExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PacienteRepositoryImpl implements PacienteRepositoryCustomSQL{

    @Autowired
    private EntityManager entityManager;


    @Override
    public List<Paciente> buscarPorNomeConsulta(String nome) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        var querry = builder.createQuery(Paciente.class);
        var root =  querry.from(Paciente.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.like(root.get("consulta") , nome+"%"));
        querry.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Paciente> pacienteTypedQuery = entityManager.createQuery(querry);

        return pacienteTypedQuery.getResultList();
    }
}
