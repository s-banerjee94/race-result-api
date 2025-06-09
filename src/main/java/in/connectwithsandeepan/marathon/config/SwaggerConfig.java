package in.connectwithsandeepan.marathon.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Marathon Results API")
                        .description("API for managing race results, users, authentication, and roles.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sandeepan")
                                .email("contactsandeepan@gmail.com")
                                .url("https://connectwithsandeepan.in")
                        )
                ).components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
