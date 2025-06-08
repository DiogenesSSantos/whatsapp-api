package com.github.dio.mensageria.listener;

import com.github.dio.mensageria.model.Paciente;

/**
 * The type Paciente rp.
 */
class PacienteRP {
    private String numeroUsuario;
    private Boolean motivoDesistencia = false;
    private String uuidUnicoUsuario;
    private Paciente paciente;

    /**
     * Instantiates a new Paciente rp.
     *
     * @param numeroUsuario    the numero usuario
     * @param uuidUnicoUsuario the uuid unico usuario
     * @param paciente         the paciente
     */
    public PacienteRP(String numeroUsuario, String uuidUnicoUsuario, Paciente paciente) {
        this.numeroUsuario = "+" + numeroUsuario;
        this.uuidUnicoUsuario = uuidUnicoUsuario;
        this.paciente = paciente;
    }

    /**
     * Gets numero usuario.
     *
     * @return the numero usuario
     */
    public String getNumeroUsuario() {
        return this.numeroUsuario;
    }

    /**
     * Sets numero usuario.
     *
     * @param numeroUsuario the numero usuario
     */
    public void setNumeroUsuario(String numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    /**
     * Gets motivo desistencia.
     *
     * @return the motivo desistencia
     */
    public Boolean getMotivoDesistencia() {
        return this.motivoDesistencia;
    }

    /**
     * Sets motivo desistencia.
     *
     * @param motivoDesistencia the motivo desistencia
     */
    public void setMotivoDesistencia(Boolean motivoDesistencia) {
        this.motivoDesistencia = motivoDesistencia;
    }

    /**
     * Gets uuid unico usuario.
     *
     * @return the uuid unico usuario
     */
    public String getUuidUnicoUsuario() {
        return this.uuidUnicoUsuario;
    }

    /**
     * Sets uuid unico usuario.
     *
     * @param uuidUnicoUsuario the uuid unico usuario
     */
    public void setUuidUnicoUsuario(String uuidUnicoUsuario) {
        this.uuidUnicoUsuario = uuidUnicoUsuario;
    }

    /**
     * Gets paciente.
     *
     * @return the paciente
     */
    public Paciente getPaciente() {
        return this.paciente;
    }

    /**
     * Sets paciente.
     *
     * @param paciente the paciente
     */
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }


}
