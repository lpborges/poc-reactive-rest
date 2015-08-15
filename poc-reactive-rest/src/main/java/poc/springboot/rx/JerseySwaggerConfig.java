package poc.springboot.rx;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.jaxrs.config.BeanConfig;

/**
* @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
*/
@Configuration
@ApplicationPath("/api")
public class JerseySwaggerConfig extends ResourceConfig {

    private static final String API_PACKAGE = "poc.springboot.rx.api";

    public JerseySwaggerConfig() {

        // Swagger
        register(io.swagger.jaxrs.listing.ApiListingResource.class);
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        packages(API_PACKAGE);
    }

    @Bean
    public BeanConfig swaggerConfig() {
        final BeanConfig swaggerConfig = new BeanConfig();
        swaggerConfig.setBasePath("/api");
        swaggerConfig.setVersion("1.0.0");
        swaggerConfig.setTitle("POC Reactive Rest API");
        swaggerConfig.setResourcePackage(API_PACKAGE);
        swaggerConfig.setScan(true);
        return swaggerConfig;
    }
}
