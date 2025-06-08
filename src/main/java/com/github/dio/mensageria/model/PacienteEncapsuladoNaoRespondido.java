package com.github.dio.mensageria.model;

import it.auties.whatsapp.model.jid.Jid;

import java.util.Objects;

/**
 * The type Paciente encapsulado nao respondido.
 */
public class PacienteEncapsuladoNaoRespondido  {

    private Paciente paciente;
    private Jid numero;


    /**
     * Instantiates a new Paciente encapsulado nao respondido.
     *
     * @param paciente the paciente
     * @param numero   the numero
     */
    public PacienteEncapsuladoNaoRespondido(Paciente paciente, Jid numero) {
        this.paciente = paciente;
        this.numero = numero;
    }

    /**
     * Gets paciente.
     *
     * @return the paciente
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Gets numero.
     *
     * @return the numero
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
