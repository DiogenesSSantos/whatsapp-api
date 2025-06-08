package com.github.dio.messageira.infraestruct.assembler;


import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import com.github.dio.messageira.model.Paciente;
import org.springframework.stereotype.Component;

@Component
public class AssemblerPaciente {

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
