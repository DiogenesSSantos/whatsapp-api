package com.github.dio.messageira.model;

import it.auties.whatsapp.model.jid.Jid;

import java.util.Objects;

public class PacienteEncapsuladoNaoRespondido  {

    private Paciente paciente;
    private Jid numero;


    public PacienteEncapsuladoNaoRespondido(Paciente paciente, Jid numero) {
        this.paciente = paciente;
        this.numero = numero;
    }

    public Paciente getPaciente() {
        return paciente;
    }

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
