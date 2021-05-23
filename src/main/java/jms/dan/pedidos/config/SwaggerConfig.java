package jms.dan.pedidos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class SwaggerConfig {
    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider(InMemorySwaggerResourcesProvider defaultResourcesProvider) {
        return () -> {
            SwaggerResource wsResource = new SwaggerResource();
            wsResource.setName("1.0.0");
            wsResource.setSwaggerVersion("2.0");
            wsResource.setLocation("/swagger.yaml");

            List<SwaggerResource> resources = new ArrayList<>();
            resources.add(wsResource);
            return resources;
        };
    }
}

