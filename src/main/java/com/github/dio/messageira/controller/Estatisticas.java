package com.github.dio.messageira.controller;


import com.github.dio.messageira.infraestruct.assembler.AssembleFiltro;
import com.github.dio.messageira.infraestruct.reports.PdfMarcacoesReports;
import com.github.dio.messageira.model.FiltroPaciente;
import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping({"/api/estatisticas"})
public class Estatisticas {
    @Autowired
    private PdfMarcacoesReports pdfMarcacoesReportsService;
    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping
    public ResponseEntity<?> consultar(@RequestParam(required = false) String nome, @RequestParam(required = false) String bairro, @RequestParam(required = false) String dataMarcacaoInicial, @RequestParam(required = false) String dataMarcacaoFinal, @RequestParam(required = false) String consulta, @RequestParam(required = false) String motivo) {
        FiltroPaciente filtroPaciente = AssembleFiltro.criandoFiltro(nome, bairro, dataMarcacaoInicial, dataMarcacaoFinal, consulta, motivo);
        List<Paciente> pacienteList = null;
        if (filtroPaciente == null) {
            pacienteList = this.pacienteRepository.findAll();
            pacienteList.sort(Comparator.naturalOrder());
            return ResponseEntity.ok().body(pacienteList);
        } else {
            return ResponseEntity.ok().body(this.pacienteRepository.filtrar(filtroPaciente));
        }
    }

    @GetMapping
    @RequestMapping(
            value = {"/pdf"},
            method = {RequestMethod.GET}
    )
    public ResponseEntity<byte[]> imprimiPDF(@RequestParam(required = false) String nome, @RequestParam(required = false) String bairro, @RequestParam(required = false) String dataMarcacaoInicial, @RequestParam(required = false) String dataMarcacaoFinal, @RequestParam(required = false) String consulta, @RequestParam(required = false) String motivo) {
        FiltroPaciente filtroPaciente = AssembleFiltro.criandoFiltro(nome, bairro, dataMarcacaoInicial, dataMarcacaoFinal, consulta, motivo);
        byte[] pdfBuscado = this.pdfMarcacoesReportsService.emitirPDF(filtroPaciente);
        HttpHeaders heards = new HttpHeaders();
        heards.add("Content-Disposition", "attachment; filename=marcacoes-diarias.pdf");
        return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(heards)).body(pdfBuscado);
    }
}
