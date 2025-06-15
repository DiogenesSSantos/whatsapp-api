package com.github.dio.mensageria.documentacao.openapi.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *  A classe Restcontroller estátistias,
 *  que nos permite acesso aos endpoins de consulta e imprimirPDF aonde está disponivel todos os dados de marcações.
 *  @hidden
 *  @author diogenesssantos.
 */
@Tag(name = "EstatisticasController", description = "Endpoints para buscar dados das marcacões")
public abstract class EstatisticasDocumentationOpenAPI {

    /**
     * Endpoint GET Consultar nos retorna os dados de marcações em JSON.
     *
     *
     * Permite filtra 1 ou n combinações facilitando a busca dos dados específicos.
     * @param nome                o nome paciente
     * @param bairro              o bairro aonde o paciente reside
     * @param dataMarcacaoInicial a data marcacao inicial
     * @param dataMarcacaoFinal   a data marcacao final
     * @param consulta            tipo de consulta
     * @param motivo              o motivo sendo possivel ser ACEITO, NÃO_RESPONDIDO, NEGADO_JUNTO_UMA_DESCRIÇÃO
     * @return JSON dos dados ou [] vazio.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "200", description = "E retorna Array[] caso não possua dados ou os dados filtrado não existam"),
    })

    @Operation(
            summary = "Consulta lista",
            description = "Consulta a lista de todos marcados por filtro ou não, se os parametros forem null retorna todos dados."
    )
    public abstract ResponseEntity<?>  consultar(@RequestParam(required = false) String nome,
                                                 @RequestParam(required = false) String bairro,
                                                 @RequestParam(required = false) String dataMarcacaoInicial,
                                                 @RequestParam(required = false) String dataMarcacaoFinal,
                                                 @RequestParam(required = false) String consulta,
                                                 @RequestParam(required = false) String motivo);


    /**
     * Endpoint GET ImprimirPDF retorna arquivo PDF para ‘download’.
     *
     *
     * Permite filtra 1 ou n combinações facilitando a busca dos dados específicos.
     * @param nome                o nome paciente
     * @param bairro              o bairro aonde o paciente reside
     * @param dataMarcacaoInicial a data marcacao inicial
     * @param dataMarcacaoFinal   a data marcacao final
     * @param consulta            tipo de consulta
     * @param motivo              o motivo sendo possivel ser ACEITO, NÃO_RESPONDIDO, NEGADO_JUNTO_UMA_DESCRIÇÃO
     *
     * caso não exista dados para o fitlro passado, vai retornar PDF em branco para ‘download’.
     *
     * @return []byte ja contendo o PDF para download .
     */
    @Operation(
            summary = "Imprimi arquivo PDF",
            description = "Imprimi todos registros pesquisado por filtro ou não retornando o  PDF filtrado, " +
                    "se os parametros forem null retorna todos dados no PDF"
    )
    public abstract ResponseEntity<byte[]> imprimiPDF(@RequestParam(required = false) String nome,
                                                      @RequestParam(required = false) String bairro,
                                                      @RequestParam(required = false) String dataMarcacaoInicial,
                                                      @RequestParam(required = false) String dataMarcacaoFinal,
                                                      @RequestParam(required = false) String consulta,
                                                      @RequestParam(required = false) String motivo);

}
