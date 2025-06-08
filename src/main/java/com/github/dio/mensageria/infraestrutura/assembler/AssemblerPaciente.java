package com.github.dio.mensageria.infraestrutura.assembler;


import com.github.dio.mensageria.model.modeloRepresentacional.PacienteMR;
import com.github.dio.mensageria.model.Paciente;
import org.springframework.stereotype.Component;

/**
 * The type Assembler paciente.
 */
@Component
public class AssemblerPaciente {

    /**
     * Disassemble to object paciente.
     *
     * @param pacienteMR the paciente mr
     * @return the paciente
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
     * Disassemble to object nao possui whatsapp paciente.
     *
     * @param pacienteMR the paciente mr
     * @return the paciente
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
