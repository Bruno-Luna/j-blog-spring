package br.com.blog.api;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer")
public class OpenApiConfig {

    private static final String TITLE_BLOG = "Blog API";
    private static final String VERSION_BLOG = "0.2.0";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(TITLE_BLOG)
                        .version(VERSION_BLOG)
                        .description("""
                            API REST para gerenciamento de um blog pessoal.
                            
                            - Registro e autenticação de usuários via JWT
                            - Operações completas de CRUD para postagens
                            - Validação de propriedade por autor
                            """)
                        .contact(new Contact()
                                .name("Bruno Luna")
                                .url("https://github.com/Bruno-Luna")
                                .email("luna.brsilva@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
