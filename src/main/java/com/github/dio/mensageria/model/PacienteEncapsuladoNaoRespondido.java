package com.github.dio.mensageria.model;

import it.auties.whatsapp.model.jid.Jid;

import java.util.Objects;

/**
 * Classe responsável encapsular o {@link Paciente} e o número de telefone em formato {@link Jid}
 * da instancia {@link it.auties.whatsapp.api.Whatsapp}
 * sendo a mesma uma classe Final, não mudança de estado após criação.
 *
 * @author diogenesssantos
 */
public class PacienteEncapsuladoNaoRespondido  {

    private Paciente paciente;
    private Jid numero;


    /**
     * A sua inicialização única mantendo o estado final.
     *
     * @param paciente o paciente
     * @param numero   o numero
     */
    public PacienteEncapsuladoNaoRespondido(Paciente paciente, Jid numero) {
        this.paciente = paciente;
        this.numero = numero;
    }


    /**
     * Gets paciente.
     *
     * @return o paciente
     */
    public Paciente getPaciente() {
        return paciente;
    }


    /**
     * Gets número.
     *
     * @return o número
     */
    public Jid getNumero() {
        return numero;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PacienteEncapsuladoNaoRespondido that = (PacienteEncapsuladoNaoRespondido) o;
        return Objects.equals(paciente, that.paciente) && Objects.equals(numero, that.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paciente, numero);
    }


}
