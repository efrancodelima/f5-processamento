package br.com.fiap.soat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenApi() {

    var tagProducao = new Tag().name("Producao")
        .description("Operações relacionadas à produção");

    var tagVideo = new Tag().name("Video")
        .description("Operações relacionadas a vídeos");

    return new OpenAPI()
        .info(new Info()
        .title("Documentação da API")
        .version("2.0")
        .description("Documentação da API do microsservico de PRODUCAO"
            + "<br>FIAP | Pós-tech | Software Architecture | Tech Challenge | Fase 4"))
        .addTagsItem(tagProducao)
        .addTagsItem(tagVideo);
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("br.com.fiap.soat.controller"))
        .paths(PathSelectors.any())
        .build();
  }
}
