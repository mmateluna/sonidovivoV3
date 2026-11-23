package cl.sonidovivo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Swagger: http://localhost:8082/swagger-ui/index.html
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI sonidoVivo() {
    String esquema = "bearer";
    return new OpenAPI()
        .info(new Info()
            .title("Sonido Vivo — Auth")
            .description("Login, registro y renovación del JWT. Las cuentas las guarda el servicio de usuarios.")
            .version("1.0"))
        .addSecurityItem(new SecurityRequirement().addList(esquema))
        .components(new Components().addSecuritySchemes(esquema, new SecurityScheme()
            .name(esquema)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")));
  }
}
