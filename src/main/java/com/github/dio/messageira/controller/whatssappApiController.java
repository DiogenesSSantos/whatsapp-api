package com.github.dio.messageira.controller;


import com.github.dio.messageira.controller.modeloRepresentacional.PacienteMR;
import com.github.dio.messageira.service.WhatsappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zap")
public class whatssappApiController {

    @Autowired
    private WhatsappService service;


    @GetMapping("/test")
    public ResponseEntity<Object> testeString() {
        return ResponseEntity.ofNullable("TESTANDO API");
    }


    @PostMapping("/enviar")
    public void enviar(@RequestBody PacienteMR pacienteMD)   {
        service.enviarMensagem(pacienteMD.getNumero() , pacienteMD.getMensagem().getTexto());
    }

    @PostMapping("/enviarBotao")
    public void enviarComBotao (@RequestBody PacienteMR pacienteMR) {
        service.enviarMensagemComBotao(pacienteMR.getNumero() , null);
    }
}
