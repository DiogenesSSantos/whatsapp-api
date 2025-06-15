package com.github.dio.mensageria.listener;

import com.github.dio.mensageria.model.Paciente;

/**
 * Classe representacional.
 *
 * @author diogenesssantos Classe modelo para representação do {@link Paciente}
 * @hidden
 */
class PacienteRP {
    private String numeroUsuario;
    private Boolean motivoDesistencia = false;
    private String uuidUnicoUsuario;
    private Paciente paciente;


    /**
     * Inicialização de um {@link PacienteRP}
     *
     * @param numeroUsuario    o número usuário
     * @param uuidUnicoUsuario o uuid único usuário
     * @param paciente         o paciente
     */
    public PacienteRP(String numeroUsuario, String uuidUnicoUsuario, Paciente paciente) {
        this.numeroUsuario = "+" + numeroUsuario;
        this.uuidUnicoUsuario = uuidUnicoUsuario;
        this.paciente = paciente;
    }


    /**
     * Gets número do usuario.
     *
     * @return the numero usuario
     */
    public String getNumeroUsuario() {
        return this.numeroUsuario;
    }

    /**
     * Sets número usuario.
     *
     * @param numeroUsuario the numero usuario
     */
    public void setNumeroUsuario(String numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    /**
     * Gets motivo desistência.
     *
     * @return o motivo desistencia
     */
    public Boolean getMotivoDesistencia() {
        return this.motivoDesistencia;
    }

    /**
     * Sets motivo desistência.
     *
     * @param motivoDesistencia o motivo desistencia
     */
    public void setMotivoDesistencia(Boolean motivoDesistencia) {
        this.motivoDesistencia = motivoDesistencia;
    }

    /**
     * Gets uuid único usuário.
     *
     * @return the uuid único usuário
     */
    public String getUuidUnicoUsuario() {
        return this.uuidUnicoUsuario;
    }

    /**
     * Sets uuid único usuário.
     *
     * @param uuidUnicoUsuario o uuid único usuario
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
