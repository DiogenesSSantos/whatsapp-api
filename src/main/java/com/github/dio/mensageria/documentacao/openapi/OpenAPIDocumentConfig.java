package com.github.dio.mensageria.documentacao.openapi;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração da documentação do OpenAPI
 * @hidden
 */
@Configuration
public class OpenAPIDocumentConfig {


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Integração WhatsApp - Agendamento de Saúde")
                        .description(" Esta API foi criada para integrar o canal de comunicação via WhatsApp com os sistemas de agendamento da Regulação de Saúde. " +
                                "A proposta é melhorar o fluxo de informações, permitindo que pacientes recebam notificações, " +
                                "\nlembretes e possam até mesmo confirmar ou reagendar compromissos diretamente pelo aplicativo. " +
                                "\nEssa abordagem visa reduzir faltas.")
                        .version("1.0.0")
                        .contact(new Contact().url("https://github.com/DiogenesSSantos")
                                .name("Diogenes").email("diogenescontatoofficial@hotmail.com"))
                        .summary("Facilita a comunicação entre serviços de agendamento e pacientes via WhatsApp")
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                        .externalDocs(new ExternalDocumentation()
                                .description("Documentação JavaDoc")
                                .url("http://devdiogenes.shop/apidocs/index.html"));


    }



    @Bean
    public GroupedOpenApi whatsappControle() {
        return GroupedOpenApi.builder()
                .group("WhatsappController")
                .pathsToMatch("/api/zap/**")
                .build();

    }


    @Bean
    public GroupedOpenApi estaticasControle() {
        return GroupedOpenApi.builder()
                .group("EstatisticasController")
                .pathsToMatch("/api/estatisticas/**")
                .build();

    }


    @Bean
    public GroupedOpenApi qrCodeControle() {
        return GroupedOpenApi.builder()
                .group("QrCodeController")
                .pathsToMatch("/api/conexao/**")
                .build();

    }



    @Bean
    public OpenApiCustomizer customizaçãoGlobalResponse() {
        return openApi -> {
            Components components = new Components();

            ApiResponse error500 = new ApiResponse()
                    .description("ERRO INTERNO SERVIDOR")
                    .content(new Content().addMediaType("application/json", new MediaType().schema(new Schema<>().$ref("http:://fodasse"))));


            components.addResponses("204", error500);

            openApi.components(components);

            openApi.getPaths().values().forEach(caminho ->
                    caminho.readOperations().forEach(operation -> {
                        ApiResponses apiResponse = operation.getResponses();
                        apiResponse.addApiResponse("204", error500);
                    }));

        };


    }
}
