package com.github.dio.messageira.controller;


import com.github.dio.messageira.infraestruct.reports.PdfMarcacoesReports;
import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController()
@RequestMapping("/api/estatisticas")
public class Estatisticas {

    @Autowired
    private PdfMarcacoesReports pdfMarcacoesReportsService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping
    public ResponseEntity<?> consultar(@RequestParam(required = false) String filtro) {
        List<Paciente> pacienteList = null;
        if (filtro == null) {
            pacienteList = pacienteRepository.findAll();
            pacienteList.sort(Comparator.naturalOrder());
            return ResponseEntity.ok()
                    .body(pacienteList);
        }
        return ResponseEntity.ok()
                .body(pacienteRepository.buscarPorNomeConsulta(filtro));
    }

    @GetMapping
    @RequestMapping(value = "/pdf" , method = RequestMethod.GET)
    public ResponseEntity<byte[]> imprimiPDF(@RequestParam(required = false) String filtro) {
        byte[] pdfBuscado = pdfMarcacoesReportsService.emitirPDF(filtro);

        var heards = new HttpHeaders();
        heards.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=marcacoes-diarias.pdf");


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .headers(heards)
                .body(pdfBuscado);
    }


}
