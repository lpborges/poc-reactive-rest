package poc.springboot.rx;

import javax.ws.rs.WebApplicationException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
*  Configuration to catch FeignExceptions and transform in WebApplicationException with the correct status code
*
* @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
*/

@Configuration
public class FeignErrorDecoderConfig {

    @Bean
    public ErrorDecoder getErrorDecoder(){
        return new HttpErrorDecoder();
    }

    private static class HttpErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            final WebApplicationException e = new WebApplicationException(response.reason(), response.status());
            return e;
        }
    }
}
