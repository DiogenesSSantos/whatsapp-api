package com.github.dio.messageira.controller.modeloRepresentacional;

import com.github.dio.messageira.model.Mensagem;
import com.github.dio.messageira.model.Paciente;
import lombok.Getter;

@Getter
public class PacienteMR extends Paciente{
    private Mensagem mensagem;

}
