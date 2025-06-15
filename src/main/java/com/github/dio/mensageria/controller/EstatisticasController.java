package com.github.dio.mensageria.controller;


import com.github.dio.mensageria.documentacao.openapi.model.EstatisticasDocumentationOpenAPI;
import com.github.dio.mensageria.infraestrutura.assembler.AssembleFiltro;
import com.github.dio.mensageria.infraestrutura.reports.PDFMarcacoesReports;
import com.github.dio.mensageria.model.FiltroPaciente;
import com.github.dio.mensageria.model.Paciente;
import com.github.dio.mensageria.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/*
 * A classe Restcontroller estátistias, que nos permite acesso aos endpoins de consulta e
 * imprimirPDF.
 * @author diogenesssantos.
 */
@RestController
@RequestMapping({"/api/estatisticas"})
public class EstatisticasController extends EstatisticasDocumentationOpenAPI {
    @Autowired
    private PDFMarcacoesReports PDFMarcacoesReportsService;
    @Autowired
    private PacienteRepository pacienteRepository;







    @GetMapping
    public ResponseEntity<?> consultar(@RequestParam(required = false) String nome, @RequestParam(required = false) String bairro, @RequestParam(required = false) String dataMarcacaoInicial, @RequestParam(required = false) String dataMarcacaoFinal, @RequestParam(required = false) String consulta, @RequestParam(required = false) String motivo) {
        FiltroPaciente filtroPaciente = AssembleFiltro.criarFiltro(nome, bairro, dataMarcacaoInicial, dataMarcacaoFinal, consulta, motivo);
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
        FiltroPaciente filtroPaciente = AssembleFiltro.criarFiltro(nome, bairro, dataMarcacaoInicial, dataMarcacaoFinal, consulta, motivo);
        byte[] pdfBuscado = this.PDFMarcacoesReportsService.emitirPDF(filtroPaciente);
        HttpHeaders heards = new HttpHeaders();
        heards.add("Content-Disposition", "attachment; filename=marcacoes-diarias.pdf");
        return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(heards)).body(pdfBuscado);
    }
}
