package com.github.dio.messageira.controller;


import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import com.github.dio.messageira.service.WhatsappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zap")
public class whatssappApiController {

    @Autowired
    private WhatsappService service;


    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/enviar")
    public void enviar(@RequestBody PacienteMR pacienteMD) throws InterruptedException {
        service.enviarMensagem(pacienteMD);
    }


    @PostMapping("/enviarList")
    public void enviarParaLista(@RequestBody List<PacienteMR> pacienteMR) {
        service.enviarMensagemLista(pacienteMR);
    }

    @DeleteMapping("/desconectar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desconectar() {
        service.desconectar();
    }

    @PutMapping("/reconectar")
    @ResponseStatus(HttpStatus.OK)
    public void reconectar() {
        service.conectar();
    }

}