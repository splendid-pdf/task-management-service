package ru.splendidpdf.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${app.docs.description}") String appDescription,
                                 @Value("${app.docs.version}") String appVersion,
                                 @Value("${app.docs.title}") String appTitle,
                                 @Value("${app.docs.server.url}") String serverUrl,
                                 @Value("${app.docs.server.description}") String serverDescription) {
        return new OpenAPI()
                .addServersItem(new Server().url(serverUrl).description(serverDescription))
                .info(new Info()
                        .title(appTitle)
                        .version(appVersion)
                        .description(appDescription));
    }

    @Bean
    public OpenApiCustomiser sortSchemasAlphabetically() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            openApi.getComponents().setSchemas(new TreeMap<>(schemas));
        };
    }
}