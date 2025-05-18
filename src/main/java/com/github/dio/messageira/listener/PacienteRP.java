package com.github.dio.messageira.listener;

import com.github.dio.messageira.model.Paciente;

class PacienteRP {
    private String numeroUsuario;
    private Boolean motivoDesistencia = false;
    private String uuidUnicoUsuario;
    private Paciente paciente;

    public PacienteRP(String numeroUsuario, String uuidUnicoUsuario, Paciente paciente) {
        this.numeroUsuario = "+" + numeroUsuario;
        this.uuidUnicoUsuario = uuidUnicoUsuario;
        this.paciente = paciente;
    }

    public String getNumeroUsuario() {
        return this.numeroUsuario;
    }

    public void setNumeroUsuario(String numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    public Boolean getMotivoDesistencia() {
        return this.motivoDesistencia;
    }

    public void setMotivoDesistencia(Boolean motivoDesistencia) {
        this.motivoDesistencia = motivoDesistencia;
    }

    public String getUuidUnicoUsuario() {
        return this.uuidUnicoUsuario;
    }

    public void setUuidUnicoUsuario(String uuidUnicoUsuario) {
        this.uuidUnicoUsuario = uuidUnicoUsuario;
    }

    public Paciente getPaciente() {
        return this.paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }


}
