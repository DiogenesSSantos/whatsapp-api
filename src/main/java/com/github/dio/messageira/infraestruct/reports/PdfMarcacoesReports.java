package com.github.dio.messageira.infraestruct.reports;


import com.github.dio.messageira.model.FiltroPaciente;
import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.repository.PacienteRepository;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Service
public class PdfMarcacoesReports {
    @Autowired
    PacienteRepository pacienteRepository;

    public byte[] emitirPDF(FiltroPaciente filtroPaciente) {
        if (filtroPaciente == null) {
            List<Paciente> pegandoTodaLista = this.pacienteRepository.findAll();
            pegandoTodaLista.sort(Comparator.naturalOrder());
            return this.preparandoJasper(pegandoTodaLista);
        } else {
            List<Paciente> pegandoListFiltrada = this.pacienteRepository.filtrar(filtroPaciente);
            pegandoListFiltrada.sort(Comparator.naturalOrder());
            return this.preparandoJasper(pegandoListFiltrada);
        }
    }

    private byte[] preparandoJasper(List<Paciente> pacienteList) {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/apiwhatsapp-marcacoes.jasper");
            HashMap<String, Object> parametros = new HashMap();
            parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pacienteList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("ERRO AO GERAR RELATÓRIO", e);
        }
    }
}
