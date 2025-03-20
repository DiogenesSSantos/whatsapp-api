package com.github.dio.messageira.infraestruct.reports;


import com.github.dio.messageira.model.Paciente;
import com.github.dio.messageira.repository.PacienteRepository;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Service
public class PdfMarcacoesReports {

    @Autowired
    PacienteRepository pacienteRepository;

    public byte[] emitirPDF(String parametro) {
        if (parametro == null) {
            var pegandoTodaLista = pacienteRepository.findAll();
            pegandoTodaLista.sort(Comparator.naturalOrder());
            return preparandoJasper(pegandoTodaLista);
        }
        var pegandoListFiltrada = pacienteRepository.buscarPorNomeConsulta(parametro);
        pegandoListFiltrada.sort(Comparator.naturalOrder());
        return preparandoJasper(pegandoListFiltrada);
    }

    private byte[] preparandoJasper(List<Paciente> pacienteList) {
        try {
            var inputStream = this.getClass().getResourceAsStream("/relatorios/apiwhatsapp-marcacoes.jasper");
            var parametros = new HashMap<String, Object>();
            parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
            var dataSource = new JRBeanCollectionDataSource(pacienteList);

            var jasperPrint = JasperFillManager.fillReport(inputStream, parametros, dataSource);


            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("ERRO AO GERAR RELATÓRIO", e);
        }
    }

}

