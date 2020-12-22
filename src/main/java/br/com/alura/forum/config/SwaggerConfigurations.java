package br.com.alura.forum.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.alura.forum.modelo.Usuario;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfigurations {

	@Bean
	public Docket forumApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("br.com.alura.forum")) //a partir de qual pacote quero que o swagger leia as classes?
				.paths(PathSelectors.ant("/**")) //quais endpoints o swagger precisa fazer analise?
				.build()
				.ignoredParameterTypes(Usuario.class) //ignore todas as URL's referentes a Usuario
				.globalOperationParameters(
                        Arrays.asList(
                                new ParameterBuilder()
                                    .name("Authorization")
                                    .description("Header para Token JWT")
                                    .modelRef(new ModelRef("string"))
                                    .parameterType("header")
                                    .required(false)
                                    .build())); 
		
		//para acessar o site que o swagger disponibiliza é: localhost:8080/swagger-ui.html
	}
	
}
