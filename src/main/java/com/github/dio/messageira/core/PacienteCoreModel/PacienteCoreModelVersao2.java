package com.github.dio.messageira.core.PacienteCoreModel;


public class PacienteCoreModelVersao2 {

    private String nome;
    private String numero;
    private String bairro;
    private String consulta;
    private String dataConsulta;
    private String motivo;


    public PacienteCoreModelVersao2(String nome, String numero, String bairro, String consulta, String dataConsulta, String motivo) {
        this.nome = nome;
        this.numero = numero;
        this.bairro = bairro;
        this.consulta = consulta;
        this.dataConsulta = dataConsulta;
        this.motivo = motivo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }



    @Override
    public String toString() {
        return "PacienteCoreModelVersao2{" +
                "nome='" + nome + '\'' +
                ", numero='" + numero + '\'' +
                ", bairro='" + bairro + '\'' +
                ", consulta='" + consulta + '\'' +
                ", dataConsulta='" + dataConsulta + '\'' +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}

