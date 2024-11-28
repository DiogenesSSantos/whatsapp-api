package com.github.dio.messageira.controller.modeloRepresentacional;

import com.github.dio.messageira.model.Mensagem;
import com.github.dio.messageira.model.Paciente;
import lombok.Getter;

import java.util.List;

@Getter
public class PacienteMR extends Paciente{
    private List<String> numeros;
    private String mensagem;
}
