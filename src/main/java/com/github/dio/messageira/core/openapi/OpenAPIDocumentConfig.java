package com.github.dio.messageira.core.openapi;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIDocumentConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("API-WHATSAPP")
                        .description("API PARA USO CONSUMIDOR")
                        .version("2.0.2")
                        .contact(new Contact().url("AAAAAAAAA").name("diogees").email("Diobotnex"))
                        .summary("ALGUMA COISA")
                        .license(new License().name("DIOTARIA").url("https://www.youtube.com/watch?v=mCZG6nRX-MQ")));


    }


    @Bean
    public GroupedOpenApi whatsappControle() {
        return GroupedOpenApi.builder()
                .group("Controle do whatsapp requisições")
                .pathsToMatch("/api/zap/**")
                .build();

    }

    @Bean
    public GroupedOpenApi estaticasControle() {
        return GroupedOpenApi.builder()
                .group("Controle do da estatisticas")
                .pathsToMatch("/api/estaticas/**")
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
