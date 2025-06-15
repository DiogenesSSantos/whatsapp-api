package com.github.dio.mensageria.infraestrutura.reports;


import com.github.dio.mensageria.model.FiltroPaciente;
import com.github.dio.mensageria.model.Paciente;
import com.github.dio.mensageria.repository.PacienteRepository;
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

/**
 * A classe responsável para gerar o nosso relátorios em PDF para o {@link com.github.dio.mensageria.controller.EstatisticasController}
 * @author diogenesssantos
 */
@Service
public class PDFMarcacoesReports {
    /**
     * Injetamos para usamos o {@link FiltroPaciente} e buscar os dados no banco de dados.
     * @hidden
     */
    @Autowired
    PacienteRepository pacienteRepository;

    /**
     * Emitir pdf.
     * @param filtroPaciente filtro personalizado para criação do relatório, se não existir dados para o filtro retorna
     *                       PDF em branco.
     * @return byte [ ]
     */
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


    /**
     * Usamos a biblioteca do <a href="https://community.jaspersoft.com/">JasperReport</a>
     * para criar os nossos relátorios, recebemos a lista de usuários para criação
     ** @param pacienteList LIST<{@link Paciente}> buscada no banco de dados.
     * @return [] byte.
     */
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
