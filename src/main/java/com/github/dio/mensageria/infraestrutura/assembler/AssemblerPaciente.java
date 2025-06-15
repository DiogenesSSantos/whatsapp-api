package com.github.dio.mensageria.infraestrutura.assembler;


import com.github.dio.mensageria.model.modeloRepresentacional.PacienteMR;
import com.github.dio.mensageria.model.Paciente;
import org.springframework.stereotype.Component;

/**
 * Classe responsável para conversão do objetos {@link PacienteMR} para {@link Paciente} assim permintido alteração
 * nos modelos representacional sem impactar na classe de persistẽncia.
 * @author diogenessantos
 */
@Component
public class AssemblerPaciente {

    /**
     * Convertemos {@link PacienteMR} para {@link Paciente}
     *
     * @param pacienteMR entrada
     * @return o paciente para persistencia de dados
     */
    public static Paciente disassembleToObject(PacienteMR pacienteMR) {
        var paciente = new Paciente();
        paciente.setCodigo(pacienteMR.getId().toString());
        paciente.setNome(pacienteMR.getNome());
        paciente.setNumero(pacienteMR.getNumeros().getFirst());
        paciente.setConsulta(pacienteMR.getConsulta());
        paciente.setDataConsulta(pacienteMR.getData());
        paciente.setMotivo("AGUARDANDO");
        paciente.setBairro(pacienteMR.getBairro());
        return paciente;
    }

    /**
     * Convertemos {@link PacienteMR} para {@link Paciente}
     * @apiNote para evitar duplicidade de código no service, foi decido casos aonde o paciente não possua o WhatsApp
     * fazendo já a conversão e persistência dos dados fixo informando nos campos justificados.
     *
     * @param pacienteMR entrada.
     * @return o paciente para persistência de dados.
     */
    public static Paciente disassembleToObjectNaoPossuiWhatsapp(PacienteMR pacienteMR) {
        var paciente = new Paciente();
        paciente.setCodigo(pacienteMR.getId().toString());
        paciente.setNome(pacienteMR.getNome());
        paciente.setNumero("NUMERO NAO EXISTE WHATSAPP");
        paciente.setConsulta(pacienteMR.getConsulta());
        paciente.setDataConsulta(pacienteMR.getData());
        paciente.setMotivo("Nao_Possui_Whatsapp");
        paciente.setBairro(pacienteMR.getBairro());
        return paciente;
    }




}
